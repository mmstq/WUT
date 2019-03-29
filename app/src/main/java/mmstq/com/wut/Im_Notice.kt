package mmstq.com.wut

import android.support.annotation.Keep

@Keep
class Im_Notice {
   var text: String? = null
   var name: String? = null
   var date: Long = 0

   constructor() {}

   constructor(text: String, name: String, date: Long) {
      this.text = text
      this.name = name
      this.date = date
   }
}
