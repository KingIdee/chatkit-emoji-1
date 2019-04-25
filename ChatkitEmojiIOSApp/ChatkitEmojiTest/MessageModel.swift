import PusherChatkit

class MessageModel {
    
    var message:PCMessage!
    var emojiModelList = [EmojiModel]()
    
}

class EmojiModel {
    var string:String?
    var count: Int?
    var usersToEmoji: [String]?
}
