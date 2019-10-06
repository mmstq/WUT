package mmstq.com.wut

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import java.util.*


class WidgetAdapter : AppWidgetProvider() {

   private val action = "mmstq.com.wut.WIDGET_UPDATE"
   private val ACTION_LEFT = "left"
   private val ACTION_RIGHT = "right"
   private val calendar = Calendar.getInstance()
   private val getDay = arrayOf("", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")


   override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
      val remoteViews = RemoteViews(context.packageName,
              R.layout.widget)
      val intent = Intent(context, WidgetAdapter::class.java)
      intent.action = action

      intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
      val pendingIntent = PendingIntent.getBroadcast(context,
              6, intent, PendingIntent.FLAG_UPDATE_CURRENT)
      remoteViews.setOnClickPendingIntent(R.id.action_button, pendingIntent)
      remoteViews.setOnClickPendingIntent(R.id.left, getPendingIntent(context, ACTION_LEFT))
      remoteViews.setOnClickPendingIntent(R.id.right, getPendingIntent(context, ACTION_RIGHT))
      appWidgetManager.updateAppWidget(appWidgetIds, remoteViews)
      context.sendBroadcast(intent)
//      super.onUpdate(context, appWidgetManager, appWidgetIds)
   }

   private fun getPendingIntent(context: Context, action: String): PendingIntent {
      val intent = Intent(context, javaClass)
      intent.action = action
      return PendingIntent.getBroadcast(context, 6, intent, 0)
   }

   override fun onReceive(context: Context, intent: Intent) {
      super.onReceive(context, intent)

      //sun = 1 sat = 7
      val sp = context.getSharedPreferences("Phone", 0)
      var day = calendar.get(Calendar.DAY_OF_WEEK)

      when {

         intent.action == action -> {
            day = calendar.get(Calendar.DAY_OF_WEEK)
            if (day == 1) day = 2

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
      sp.edit().putInt("day_int", day).apply()
      val content = Constant.getValue(context, day.toString())
      Constant.list = content!!.split(",").toTypedArray()


      val svcIntent = Intent(context, WidgetService::class.java)
      svcIntent.putExtra("list", Constant.list)
      val appWidgetManager = AppWidgetManager.getInstance(context)
      val widget = ComponentName(context, WidgetAdapter::class.java)
      val remoteViews = RemoteViews(context.packageName, R.layout.widget)
      val widgetIds = appWidgetManager.getAppWidgetIds(widget)
      remoteViews.setTextViewText(R.id.action_button, getDay[day])
      remoteViews.setRemoteAdapter(R.id.gv, svcIntent)
      appWidgetManager.notifyAppWidgetViewDataChanged(widgetIds, R.id.gv)
      appWidgetManager.updateAppWidget(widget, remoteViews)

   }
}
