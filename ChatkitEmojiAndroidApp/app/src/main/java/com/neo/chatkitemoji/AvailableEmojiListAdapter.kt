package com.neo.chatkitemoji

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class AvailableEmojiListAdapter (var emojiList: ArrayList<String>,
                                 var availableEmojiClickListener: AvailableEmojiClickListener)
    : RecyclerView.Adapter<AvailableEmojiListAdapter.ViewHolder>() {

    fun addEmoji(model:String){
        this.emojiList.add(model)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableEmojiListAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(android.R.layout.simple_expandable_list_item_1, parent, false))
    }

    override fun onBindViewHolder(holder: AvailableEmojiListAdapter.ViewHolder, position: Int) = holder.bind(emojiList[position])


    override fun getItemCount(): Int = emojiList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val emojiString: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(item: String) = with(itemView) {
            emojiString.text =  item

            this.setOnClickListener {
                availableEmojiClickListener.onEmojiClicked(item)
            }
        }

    }

    interface AvailableEmojiClickListener {
        fun onEmojiClicked(selectedEmoji:String)
    }

}