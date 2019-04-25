package com.neo.chatkitemoji

import android.app.Application
import com.pusher.chatkit.CurrentUser

class ChatEmojiApp: Application(){

    companion object {
        lateinit var currentUser: CurrentUser
    }
}