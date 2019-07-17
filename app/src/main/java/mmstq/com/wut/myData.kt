package mmstq.com.wut

import androidx.annotation.Keep
import java.io.Serializable

@Keep
class myData : Serializable {
   var heading: String? = null
   var description: String? = null
   var admin: String? = null
   var surveyNo: Long = 0
   var isSay: Boolean = false
   var isWutOptional: Boolean = false
   var isLikeDislike: Boolean = false
   var isWut: Boolean = false
   var isOpen: Boolean = false
   var noDislike: Int = 0
   var noLike: Int = 0
   var noSay: Int = 0
   var noWut: Int = 0

   constructor()

   constructor(surveyNo: Long, noDislike: Int, noLike: Int, noWut: Int, noSay: Int, heading: String, description: String, admin: String, say: Boolean, wutOptional: Boolean, likeDislike: Boolean, wut: Boolean, open: Boolean) {
      this.heading = heading
      this.admin = admin
      this.description = description
      this.isSay = say
      this.isWutOptional = wutOptional
      this.isLikeDislike = likeDislike
      this.isWut = wut
      this.isOpen = open
      this.surveyNo = surveyNo
      this.noWut = noWut
      this.noDislike = noDislike
      this.noLike = noLike
      this.noSay = noSay
   }

}
