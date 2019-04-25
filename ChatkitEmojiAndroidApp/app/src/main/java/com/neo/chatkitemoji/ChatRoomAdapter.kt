package com.neo.chatkitemoji

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class ChatRoomAdapter(val chatMessageClickListener: ChatMessageClickListener): RecyclerView.Adapter<ChatRoomAdapter.ViewHolder>() {

    private var messageList = ArrayList<MessageModel>()

    fun addMessage(model:MessageModel){
        this.messageList.add(model)
        notifyDataSetChanged()
    }

    fun getList() = messageList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.chat_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: ChatRoomAdapter.ViewHolder, position: Int) = holder.bind(messageList[position])

    override fun getItemCount() = messageList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val username: TextView = itemView.findViewById(R.id.editTextUsername)
        private val message: TextView = itemView.findViewById(R.id.editTextMessage)
        private val recyclerView: RecyclerView = itemView.findViewById(R.id.emojiRecyclerView)

        fun bind(item: MessageModel) = with(itemView) {
            username.text = item.message.userId
            message.text = item.message.text

            val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            recyclerView.layoutManager = linearLayoutManager
            recyclerView.adapter = ChatRoomEmojiListAdapter(item.data)

            this.setOnClickListener {
                chatMessageClickListener.onMessageClicked(adapterPosition,item)
            }
        }

    }

    interface ChatMessageClickListener {
        fun onMessageClicked(position:Int, item: MessageModel)
    }

}
