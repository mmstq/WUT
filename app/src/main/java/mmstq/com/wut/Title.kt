package mmstq.com.wut

import java.util.ArrayList
import java.util.HashMap

object Title {
   fun getTitle(map: HashMap<String, List<String>>): List<String> {
      return ArrayList(map.keys)
   }
}
