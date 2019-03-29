package mmstq.com.wut

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.support.v4.app.NotificationCompat
import android.util.Log

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MS : FirebaseMessagingService() {

   override fun onMessageReceived(remoteMessage: RemoteMessage?) {

      //Calling method topsb generate notification
      sendNotification(remoteMessage!!.notification!!.title,
              remoteMessage.notification!!.body)
   }

   //This method is only generating push notification
   private fun sendNotification(messageTitle: String?, messageBody: String?) {
      val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
      val notificationBuilder = NotificationCompat.Builder(this)
              .setSmallIcon(R.drawable.msg)
              .setContentTitle(messageTitle)
              .setContentText(messageBody)
              .setPriority(Notification.PRIORITY_HIGH)
              .setAutoCancel(false)
              .setSound(defaultSoundUri)

      val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.notify(count, notificationBuilder.build())
      count++
   }

   companion object {
      private var count = 0
   }


}
