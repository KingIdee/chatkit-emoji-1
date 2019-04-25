package com.neo.chatkitemoji

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import com.google.gson.Gson
import com.pusher.chatkit.CurrentUser
import com.pusher.chatkit.rooms.RoomListeners
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import kotlinx.android.synthetic.main.activity_chat_room.*
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChatRoomActivity : AppCompatActivity(), ChatRoomAdapter.ChatMessageClickListener {

    lateinit var currentUser: CurrentUser
    var currentRow = -1
    private val chatRoomAdapter = ChatRoomAdapter(this)
    private lateinit var options: PusherOptions
    private lateinit var pusher: Pusher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        currentUser = ChatEmojiApp.currentUser
        setupRecyclerView()
        subscribeToRoom()
        setupPusher()
    }


    private fun setupRecyclerView() {
        with(recyclerViewChat){
            layoutManager = LinearLayoutManager(this@ChatRoomActivity)
            adapter = chatRoomAdapter
            addItemDecoration(DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL))
        }
    }


    private fun subscribeToRoom() {

        currentUser.subscribeToRoom(
            roomId = currentUser.rooms[0].id,
            listeners = RoomListeners(
                onMessage = {message ->
                    runOnUiThread {
                        chatRoomAdapter.addMessage(MessageModel(message, ArrayList()))
                    }
                }
            ),
            callback = { subscription ->
            },
            messageLimit = 20
        )

    }


    private fun setupPusher() {
        options = PusherOptions()
        options.setCluster("eu")
        pusher = Pusher("2663259f85e02b7dde58", options)
        pusher.connect()
        pusherSubscribe()
    }



    private fun pusherSubscribe() {
        val channel = pusher.subscribe(currentUser.rooms[0].id)

        channel.bind(
            "emoji-event"
        ) { channelName, eventName, data ->

            val result = JSONObject(data.toString())

            runOnUiThread {

                val chatMessagesIterator = chatRoomAdapter.getList().iterator()

                while (chatMessagesIterator.hasNext()){

                    val messageItem = chatMessagesIterator.next()
                    if (messageItem.message.id.toString() == result.getString("messageId")) {
                        var doesEmojiExist = false
                        val emojiListIterator: MutableIterator<EmojiModel> = messageItem.data.iterator()

                        while (emojiListIterator.hasNext()){
                            val currentValue = emojiListIterator.next()
                            if(result.getString("emoji")== currentValue.string) {
                                // update with new contents
                                doesEmojiExist = true
                                if(result.getString("count").toInt()==0){
                                    emojiListIterator.remove()
                                } else {
                                    currentValue.count = result.getString("count").toInt()
                                    currentValue.userIds = Gson().fromJson(result.getString("userIds"),
                                        ArrayList::class.java) as ArrayList<String>

                                }

                            }

                        }

                        if (!doesEmojiExist){
                            val newModel = EmojiModel()
                            newModel.string = result.getString("emoji")
                            newModel.userIds = Gson().fromJson(result.getString("userIds"),
                                ArrayList::class.java) as ArrayList<String>

                            messageItem.data.add(newModel)
                        }

                    }

                }
                chatRoomAdapter.notifyDataSetChanged()
            }

        }

    }


    override fun onMessageClicked(position:Int,item: MessageModel) {
        currentRow = position
        startActivityForResult(Intent(this,EmojiActivity::class.java),1000)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==1000 && resultCode == Activity.RESULT_OK){
            var ifEmojiExists = false
            val result = data!!.getStringExtra("result")

            for (item in chatRoomAdapter.getList()[currentRow].data){

                if(item.string == result) {
                    ifEmojiExists = true

                    if(!item.userIds.contains(currentUser.id)){
                        item.userIds.add(currentUser.id)
                        item.count.inc()
                        sendToPusher(result, item.count.inc(), item.userIds)
                    } else {
                        item.userIds.remove(currentUser.id)
                        item.count = if (item.count > 0) item.count.dec() else 0
                        sendToPusher(result,item.count ,item.userIds)
                    }

                }

            }

            if (!ifEmojiExists){
                val userIds = ArrayList<String>()
                userIds.add(currentUser.id)
                sendToPusher(result, 1, userIds)
            }

        }

    }


    private fun sendToPusher(
        result: String,
        count: Int,
        userIds: ArrayList<String>
    ) {

        val reqObject = JSONObject()
        reqObject.put("messageId",chatRoomAdapter.getList()[currentRow].message.id.toString())
        reqObject.put("roomId", currentUser.rooms[0].id)
        reqObject.put("emoji",result)
        reqObject.put("count",count.toString())
        reqObject.put("userIds", Gson().toJson(userIds))

        val body =
            RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                reqObject.toString())

        RetrofitInstance.retrofit.updateEmoji(body).enqueue(object: Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {

            }

            override fun onResponse(call: Call<String>, response: Response<String>) {

            }

        })

    }

}