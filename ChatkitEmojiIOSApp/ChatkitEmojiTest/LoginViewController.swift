//

import UIKit
import Alamofire
import PusherChatkit

class LoginViewController: UIViewController {

    
    var chatManager: ChatManager!
    var currentUser: PCCurrentUser?
    var chatManagerDelegate: PCChatManagerDelegate?
    
    @IBOutlet weak var userTextField: UITextField!
    
    @IBAction func signIn(_ sender: Any) {
        if(!((userTextField.text?.isEmpty)!)){
            
            Alamofire.request("http://localhost:3000/users",
                              method: .post,
                              parameters: ["userId": userTextField.text!], encoding: JSONEncoding.default)
                .validate()
                .responseJSON { response in
                    if(response.response!.statusCode==200) {
                        self.setupChatManager()
                    }
            }
            
        }
    }
   
    
    
    
    func setupChatManager() {
        self.chatManager = ChatManager(
            instanceLocator: "v1:us1:ffa1e6ad-5dba-40a8-9d2a-a2868a8879bb",
            tokenProvider: PCTokenProvider(url: "http://localhost:3000/token"),
            userID: self.userTextField.text!
        )
        
        self.chatManagerDelegate = MyChatManagerDelegate()
        
        self.chatManager.connect(
            delegate: self.chatManagerDelegate!
        ) { [unowned self] currentUser, error in
            guard error == nil else {
                print("Error connecting: \(error!.localizedDescription)")
                return
            }
            
            guard let currentUser = currentUser else {
                print("CurrentUser object is nil")
                return
            }
            self.currentUser = currentUser
            
            DispatchQueue.main.async() {
                let storyBoard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
                let newViewController = storyBoard.instantiateViewController(withIdentifier:"chatRoomViewController")
                    as! ChatRoomViewController
                
                newViewController.currentUser = currentUser
                
                let navigationController = UINavigationController(rootViewController: newViewController)
                self.present(navigationController, animated: true, completion: nil)
                
            }
            
        }
    }


}

class MyChatManagerDelegate: PCChatManagerDelegate {}
