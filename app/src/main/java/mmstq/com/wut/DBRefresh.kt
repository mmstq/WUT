package mmstq.com.wut

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters


class DBRefresh(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
   private val con = context
   override fun doWork(): ListenableWorker.Result {
      Constant.dbRefresh()
      sendNotification(con)
      return ListenableWorker.Result.success()
   }

   private fun sendNotification(context: Context) {
      val channelID = "id_02"
      var channel: NotificationChannel? = null

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         channel = NotificationChannel(channelID, "Events", NotificationManager.IMPORTANCE_HIGH)
      }

      val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
      val notificationBuilder = NotificationCompat.Builder(context)
              .setSmallIcon(R.drawable.admin)
              .setContentTitle("WorkManager")
              .setPriority(Notification.PRIORITY_HIGH)
              .setAutoCancel(false)
              .setChannelId(channelID)
              .setSound(defaultSoundUri)

      val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         notificationManager.createNotificationChannel(channel!!)
      }
      notificationManager.notify(1, notificationBuilder.build())
   }
}
