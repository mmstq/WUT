package mmstq.com.wut

import android.content.Context
import android.content.SharedPreferences
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import android.widget.Toast

class CustomAdapter(private val mContext: Context, private var web: Array<String>) : RemoteViewsService.RemoteViewsFactory {

   private var content:String = ""
   private val sharedPreferences = mContext.getSharedPreferences("Phone", 0)

   override fun getViewAt(p0: Int): RemoteViews {

      val remoteViews = RemoteViews(mContext.packageName, R.layout.dm)

      content = web[p0]
      content = when {

         content.contains("(III)") -> {
            remoteViews.setImageViewResource(R.id.groups, R.drawable.threedots)
            content.replace("(III)", "")
         }
         content.contains("(II)") -> {
            remoteViews.setImageViewResource(R.id.groups, R.drawable.twodots)
            content.replace("(II)", "")
         }
         content.contains("(I)") -> {
            remoteViews.setImageViewResource(R.id.groups, R.drawable.onedot)
            content.replace("(I)", "")
         }
         else -> {
            remoteViews.setImageViewResource(R.id.groups, R.drawable.nodots)
            content
         }
      }
      sharedPreferences.edit().putString("content", content).apply()
      remoteViews.setTextViewText(R.id.lable, content)
      return remoteViews
   }

   override fun getCount(): Int {
      return web.size
   }

   override fun getItemId(position: Int): Long {
      return (position.toLong())
   }

   override fun onCreate() {
   }

   override fun onDataSetChanged() {
      web = Constant.list!!
   }

   override fun hasStableIds(): Boolean {
      return true
   }

   override fun getViewTypeCount(): Int {
      return 1
   }

   override fun onDestroy() {
   }

   override fun getLoadingView(): RemoteViews? {
      val remoteViews = RemoteViews(mContext.packageName, R.layout.dm)
      sharedPreferences.getString("content", "Loading")
      remoteViews.setTextViewText(R.id.lable, content)
      return remoteViews

   }
}
