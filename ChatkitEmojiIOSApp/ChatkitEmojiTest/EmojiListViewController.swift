
import UIKit

class EmojiListViewController: UICollectionViewController {
    
    var callback : ((String)->())?
    var emojiList = [String]()
    
    override func collectionView(_ collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return emojiList.count
    }
    
    override func collectionView(_ collectionView: UICollectionView, didSelectItemAt indexPath: IndexPath) {
        callback?(emojiList[indexPath.row])
        self.dismiss(animated: true, completion: nil)
    }
    
    override func collectionView(_ collectionView: UICollectionView, cellForItemAt indexPath: IndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCell(withReuseIdentifier: "emojiCell", for: indexPath) as! EmojiCell
        
        cell.emojiText.text = emojiList[indexPath.row]
        return cell
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        emojiList.append("😉")
        emojiList.append("😡")
        emojiList.append("👺")
        emojiList.append("🥰")
        emojiList.append("😡")
        
        self.collectionView.reloadData()
    }
    
}
