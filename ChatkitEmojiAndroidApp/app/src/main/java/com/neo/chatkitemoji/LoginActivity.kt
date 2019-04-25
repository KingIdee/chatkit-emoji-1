package com.neo.chatkitemoji


import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.pusher.chatkit.AndroidChatkitDependencies
import com.pusher.chatkit.ChatManager
import com.pusher.chatkit.ChatkitTokenProvider
import kotlinx.android.synthetic.main.activity_login.*
import okhttp3.MediaType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import org.json.JSONObject
import okhttp3.RequestBody

class LoginActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        signIn.setOnClickListener {
            val reqObject = JSONObject()
            reqObject.put("userId", editTextUsername.text.toString())

            val body =
                RequestBody.create(MediaType.parse("application/json; charset=utf-8"),
                    reqObject.toString())

            RetrofitInstance.retrofit.createUser(body).enqueue(object:Callback<String>{
                override fun onFailure(call: Call<String>, t: Throwable) {
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.code()==200){
                        Log.d("Idee","Call setupManager")

                        setupChatManager()
                    } else {

                    }
                }

            })


        }
    }


    private fun setupChatManager() {
        val chatManager = ChatManager(
            instanceLocator = "v1:us1:ffa1e6ad-5dba-40a8-9d2a-a2868a8879bb",
            userId = editTextUsername.text.toString(),
            dependencies = AndroidChatkitDependencies(
                tokenProvider = ChatkitTokenProvider(
                    endpoint = "http://10.0.2.2:3000/token",
                    userId = editTextUsername.text.toString()
                )
            )
        )

        chatManager.connect { result ->
            when (result) {
                is com.pusher.util.Result.Success -> {
                    ChatEmojiApp.currentUser = result.value
                    startActivity(Intent(this@LoginActivity,ChatRoomActivity::class.java))
                }

                is com.pusher.util.Result.Failure -> {

                }
            }
        }

    }


}
