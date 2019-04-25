package com.neo.chatkitemoji

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_emoji.*

class EmojiActivity : AppCompatActivity(), AvailableEmojiListAdapter.AvailableEmojiClickListener {

    private val availableEmojiAdapter = AvailableEmojiListAdapter(ArrayList(), this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emoji)
        setupRecyclerView()
        addEmojis()
    }

    override fun onEmojiClicked(selectedEmoji: String) {
        val resultIntent = Intent()
        resultIntent.putExtra("result",selectedEmoji)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun addEmojis() {
        availableEmojiAdapter.addEmoji("\uD83D\uDE09") // wink
        availableEmojiAdapter.addEmoji("\uD83D\uDE20") // angry
        availableEmojiAdapter.addEmoji("\uD83D\uDE02") // face with tears of joy
        availableEmojiAdapter.addEmoji("\uD83D\uDE08") // smiling face with horns
        availableEmojiAdapter.addEmoji("\uD83D\uDC7A") // goblin
    }

    private fun setupRecyclerView() {

        with(availableEmojiRecyclerView){
            layoutManager = GridLayoutManager(this@EmojiActivity,3)
            adapter = availableEmojiAdapter
        }

    }
}