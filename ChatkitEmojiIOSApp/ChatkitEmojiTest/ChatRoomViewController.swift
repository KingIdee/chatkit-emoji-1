
import UIKit
import Foundation
import PusherChatkit
import Alamofire
import PusherSwift

class ChatRoomViewController: UITableViewController {
    
    var currentUser: PCCurrentUser?
    var messageList: [MessageModel] = []
    var currentRow = -1
    var options:PusherClientOptions?
    var pusher: Pusher?
    
    @IBOutlet var tableview: UITableView!
    
    override func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 80
    }
    
    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return messageList.count
    }
    
    override func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        currentRow = indexPath.row
        self.performSegue(withIdentifier: "openEmojiList", sender: self)
    }
    
    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "messageCell", for: indexPath) as! MessageCell
        
        cell.setItems(model: messageList[indexPath.row])
        return cell
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        navigationController?.navigationBar.prefersLargeTitles = true
        subscribeToRoom()
        setupPusher()
    }
    
    func subscribeToRoom() {
        
        currentUser!.subscribeToRoom(
            room: currentUser!.rooms[0],
            roomDelegate: self
        ) { error in
            guard error == nil else {
                print("Error subscribing to room: \(error!.localizedDescription)")
                return
            }
            print("Subscribed to room!")
            
        }
        
    }
    
    func setupPusher(){
        
        options = PusherClientOptions(
            host: .cluster("eu")
        )

        pusher = Pusher(
            key: "2663259f85e02b7dde58",
            options: self.options!
        )

        pusher!.connect()
        pusherSubscribe()
    }
    
    func pusherSubscribe(){
        
        let channel = pusher!.subscribe((self.currentUser?.rooms[0].id)!)
        
        let _ = channel.bind(eventName: "emoji-event", callback: { (data: Any?) -> Void in
            if let data = data as? [String : AnyObject] {
                
                for message in self.messageList {

                    if (String(message.message.id) == String((data["messageId"] as? Int)!) ) {
                        
                        var doesEmojiExist = false

                        for (index, emojiItem) in message.emojiModelList.enumerated() {
                            
                            if(data["emoji"] as? String == emojiItem.string){
                                doesEmojiExist = true
                                if( (data["count"] as? Int) == 0 ){
                                    message.emojiModelList.remove(at: index)
                                } else {
                                    emojiItem.count = data["count"] as? Int
                                    //currentValue.userIds = Gson().fromJson(result.getString("userIds"),
                                    
                                }
                                
                            }
                            
                        }
                        
                        
                        
                        if (!doesEmojiExist){
                            
                            let newModel = EmojiModel()
                            
                            newModel.string = data["emoji"] as? String
                            newModel.count = data["count"] as? Int
                            newModel.usersToEmoji = String((data["userIds"]! as? String)!).toJSON() as? [String]
                            
                            message.emojiModelList.append(newModel)
                        }
                        
                    }
                    
                    
                    
                }
                
                self.tableView.reloadData()
                
                
            }
        })
    }
    
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        
        if segue.identifier == "openEmojiList" {
            let emojiListViewController = segue.destination as! EmojiListViewController
            emojiListViewController.callback = { result in
                print(result)
                
                var ifEmojiExists = false;
                
                for emoji in self.messageList[self.currentRow].emojiModelList {
                    if(emoji.string==result){
                        ifEmojiExists = true
                        if(!(emoji.usersToEmoji!.contains((self.currentUser?.id)!))){
                            
                            emoji.usersToEmoji!.append((self.currentUser?.id)!)
                            let newCount = emoji.count! + 1
                            self.sendToPusher(emojiString: emoji.string!, count: newCount, users: emoji.usersToEmoji!)
                            
                        } else {
                            
                            emoji.usersToEmoji!.removeAll { $0 == self.currentUser?.id }
                            
                            if (emoji.count! > 0) {
                                emoji.count = emoji.count! - 1
                            } else {
                                emoji.count = 0
                            }
                            
                            self.sendToPusher(emojiString: result, count: emoji.count!, users: emoji.usersToEmoji!)
                            
                        }
                    }
                }
                
                if (!ifEmojiExists) {
                    var userIds = [String]()
                    userIds.append(self.currentUser!.id)
                    self.sendToPusher(emojiString: result, count: 1, users: userIds)
                }
                
                self.tableview.reloadData()
                
            }
            
        }
    }
    
    
    func sendToPusher(emojiString: String, count:Int, users:[String]){
        
        Alamofire.request("http://localhost:3000/updateEmoji",
                          method: .post,
                          parameters: ["messageId": messageList[currentRow].message.id, "roomId": self.currentUser!.rooms[0].id, "emoji": emojiString, "count":count,
                                       "userIds":toJsonString(from: users) as Any], encoding: JSONEncoding.default)
            .validate()
            .responseJSON { response in
                if(response.response!.statusCode==200) {
                }
        }
        
    }
    
    
    func toJsonString(from object:Any) -> String? {
        guard let data = try? JSONSerialization.data(withJSONObject: object, options: []) else {
            return nil
        }
        return String(data: data, encoding: String.Encoding.utf8)
    }
    
    
}

extension ChatRoomViewController: PCRoomDelegate {
    func onMessage(_ message: PCMessage) {
        let item = MessageModel()
        item.message = message
        self.messageList.insert(item, at:0)
        DispatchQueue.main.async {
            self.tableView.reloadData()
        }
    }
    
}

extension String {
    func toJSON() -> Any? {
        guard let data = self.data(using: .utf8, allowLossyConversion: false) else { return nil }
        return try? JSONSerialization.jsonObject(with: data, options: .mutableContainers)
    }
}
