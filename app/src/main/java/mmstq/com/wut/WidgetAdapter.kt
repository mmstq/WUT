package mmstq.com.wut

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import java.util.*


class WidgetAdapter : AppWidgetProvider() {

   private var myAlarm = MyAlarm()
   private val action = "mmstq.com.wut.WIDGET_UPDATE"
   private val getDay = arrayOf("Update", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")


   override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
      val remoteViews = RemoteViews(context.packageName,
              R.layout.widget)
      val intent = Intent(context, WidgetAdapter::class.java)
      intent.action = action
      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
      val pendingIntent = PendingIntent.getBroadcast(context,
              0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
      remoteViews.setOnClickPendingIntent(R.id.action_button, pendingIntent)
      remoteViews.setOnClickPendingIntent(R.id.left, getPendingIntent(context, "left"))
      remoteViews.setOnClickPendingIntent(R.id.right, getPendingIntent(context, "right"))
      appWidgetManager.updateAppWidget(appWidgetIds, remoteViews)
      context.sendBroadcast(intent)
   }

   private fun getPendingIntent(context: Context, action: String): PendingIntent {
      val intent = Intent(context, javaClass)
      intent.action = action
      return PendingIntent.getBroadcast(context, 0, intent, 0)
   }

   override fun onReceive(context: Context, intent: Intent) {
      super.onReceive(context, intent)
      //sun = 1 sat = 7
      val calendar = Calendar.getInstance()
      val sp = context.getSharedPreferences("Phone", 0)
      var day = calendar.get(Calendar.DAY_OF_WEEK)

      when {

         intent.action == action -> {
            day = calendar.get(Calendar.DAY_OF_WEEK)
         }

         intent.action == "right" -> {

            day = sp.getInt("day_int", calendar.get(Calendar.DAY_OF_WEEK))
            ++day
            if (day == 8) day = 2

         }
         intent.action == "left" -> {

            day = sp.getInt("day_int", calendar.get(Calendar.DAY_OF_WEEK))
            --day
            if (day == 1 || day == 0) day = 7

         }
      }
      sp.edit().putInt("day_int",day).apply()
      var content = myAlarm.getValue(context, day.toString())

      if (content != null) {
         content = content.replace("/", "\n")
         content = content.replace(":", "->")
      }

      val appWidgetManager = AppWidgetManager.getInstance(context)
      val widget = ComponentName(context, WidgetAdapter::class.java)
      val remoteViews = RemoteViews(context.packageName, R.layout.widget)
      remoteViews.setTextViewText(R.id.textview, content)
      remoteViews.setTextViewText(R.id.action_button, getDay[day])
      appWidgetManager.updateAppWidget(widget, remoteViews)

   }
}
