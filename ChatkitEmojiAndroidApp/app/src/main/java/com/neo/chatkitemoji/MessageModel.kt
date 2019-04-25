package com.neo.chatkitemoji

import com.pusher.chatkit.messages.Message

data class MessageModel(val message: Message, val data:ArrayList<EmojiModel>)

data class EmojiModel (var string :String = "",
                       var count :Int = 1,
                       var userIds :ArrayList<String> = ArrayList())