
import UIKit
import PusherChatkit

class MessageCell : UITableViewCell {
    
    @IBOutlet weak var username: UILabel!
    @IBOutlet weak var message: UILabel!
    @IBOutlet weak var selectedEmojis: UILabel!
    
    func setItems(model:MessageModel){
        
        var emojiString = ""
        for item in model.emojiModelList {
            emojiString = emojiString + item.string! + String(item.count!) + " "
        }
        selectedEmojis.text = emojiString
        message.text = model.message.text
        username.text = model.message.sender.id
        
    }
    
}
