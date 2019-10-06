package mmstq.com.wut

import android.content.Intent
import android.widget.RemoteViewsService

class WidgetService : RemoteViewsService() {
   override fun onGetViewFactory(p0: Intent?): RemoteViewsFactory {
      val list = p0!!.getStringArrayExtra("list")
      return(CustomAdapter(applicationContext, list!!))
   }

}