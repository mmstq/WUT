package mmstq.com.wut

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.PowerManager
import android.support.v4.app.NotificationCompat
import android.util.Log
import java.util.*


class MyAlarm : BroadcastReceiver() {
   private lateinit var calendar: Calendar

   override fun onReceive(context: Context, intent: Intent) {

      val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
      if (!pm.isScreenOn) {
         @SuppressLint("InvalidWakeLockTag") val wl = pm.newWakeLock(
                 PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ON_AFTER_RELEASE, "wakeup")
         wl.acquire(5000)
      }
      val notifyID = intent.getIntExtra("Notification_ID", 0)
      calendar = Calendar.getInstance()
      val day = calendar.get(Calendar.DAY_OF_WEEK)
      setAlarm(notifyID, context)
      val heading: String
      val desc: String?
      val icon: Int
      when (notifyID) {

         1 -> {
            heading = "Today In Lunch"
            desc = getValue(context, day.toString() + notifyID.toString())
            icon = R.drawable.lunch
            sendNotification(heading, desc, context, notifyID, icon)
         }
         2 -> {
            heading = "Today In Dinner"
            desc = getValue(context, day.toString() + notifyID.toString())
            icon = R.drawable.dinner
            sendNotification(heading, desc, context, notifyID, icon)
         }
         0 -> {
            heading = "Today In Breakfast"
            desc = getValue(context, day.toString() + notifyID.toString())
            icon = R.drawable.breakfast
            sendNotification(heading, desc, context, notifyID, icon)
         }
         3 -> {
            if (day != 1) {
               heading = "Today's Time Table"
               desc = getValue(context, day.toString())
               icon = R.drawable.timetable
               sendNotification(heading, desc!!.replace("/", "\n"), context, notifyID, icon)
            }
         }
         4 -> {
            heading = "Tomorrow Is Holiday"
            val key = calendar.get(Calendar.DATE).toString() + "/" + (calendar.get(Calendar.MONTH) + 1)

            desc = getValue(context, key)
            if (desc != null) {
               icon = R.drawable.holiday
               sendNotification(heading, desc, context, notifyID, icon)
            }
         }
         else -> { }
      }
   }

   private fun sendNotification(title: String, body: String?, context: Context, notify_ID: Int, icon: Int) {
      val channelID = "id_01"
      var channel: NotificationChannel? = null

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         channel = NotificationChannel(channelID, "Events", NotificationManager.IMPORTANCE_HIGH)
      }

      val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
      val notificationBuilder = NotificationCompat.Builder(context)
              .setSmallIcon(icon)
              .setContentTitle(title)
              .setContentText(body)
              .setPriority(Notification.PRIORITY_HIGH)
              .setAutoCancel(false)
              .setChannelId(channelID)
              .setSound(defaultSoundUri)
              .setStyle(NotificationCompat.BigTextStyle().bigText(body))

      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         notificationManager.createNotificationChannel(channel!!)
      }
      notificationManager.notify(notify_ID, notificationBuilder.build())
   }

   fun getValue(context: Context, key: String): String? {
      val mDB = AppDatabase.getInstance(context)
      return mDB?.rawDao()?.getByKey(key)?.value

   }

   private fun setAlarm(id: Int, context: Context) {
      val date = Date()
      calendar.time = date

      calendar.add(Calendar.DATE, 1)

      val i = Intent(context, MyAlarm::class.java)
      i.putExtra("Notification_ID", id)
      val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
      val pi = PendingIntent.getBroadcast(context, id, i, PendingIntent.FLAG_UPDATE_CURRENT)

      //setting the repeating alarm that will be fired every day

      when {
         Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pi)
         Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> am.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pi)
         else -> am.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pi)
      }
   }
}

