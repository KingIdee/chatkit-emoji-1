package com.neo.chatkitemoji

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class ChatRoomEmojiListAdapter(var emojiList: ArrayList<EmojiModel>)
    : RecyclerView.Adapter<ChatRoomEmojiListAdapter.ViewHolder>() {

    fun addEmoji(model:EmojiModel){
        this.emojiList.add(model)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomEmojiListAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.emoji_item, parent, false))
    }

    override fun onBindViewHolder(holder: ChatRoomEmojiListAdapter.ViewHolder, position: Int) = holder.bind(emojiList.elementAt(position))


    override fun getItemCount(): Int = emojiList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val emojiString: TextView = itemView.findViewById(R.id.emojiString)
        private val emojiCount: TextView = itemView.findViewById(R.id.emojiCount)

        fun bind(item: EmojiModel) = with(itemView) {
            emojiString.text =  item.string
            emojiCount.text =  item.count.toString()
        }

    }

}