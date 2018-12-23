package mmstq.com.wut;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class MS extends FirebaseMessagingService {
    private static int count = 0;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        //Calling method to generate notification
        sendNotification(remoteMessage.getNotification().getTitle(),
                remoteMessage.getNotification().getBody());
    }

        //This method is only generating push notification
    public void sendNotification(String messageTitle, String messageBody) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.msg)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setPriority(Notification.PRIORITY_HIGH)
                .setAutoCancel(false)
                .setSound(defaultSoundUri);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
       if (notificationManager != null) {
          notificationManager.notify(count, notificationBuilder.build());
       }
       count++;
    }


}
