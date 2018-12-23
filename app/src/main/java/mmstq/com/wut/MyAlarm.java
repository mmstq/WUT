package mmstq.com.wut;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import static android.content.Context.MODE_PRIVATE;


public class MyAlarm extends BroadcastReceiver {
   Calendar calendar;

   @Override
   public void onReceive(Context context, Intent intent) {

      PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
      if (pm != null && !pm.isScreenOn()) {
         @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(
         		  PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "wakeup");
         wl.acquire(5000);
      }
      int notify_ID = intent.getIntExtra("Notification_ID", 0);
      calendar = Calendar.getInstance();
      int day = calendar.get(Calendar.DAY_OF_WEEK);
      setAlarm(notify_ID,context);
      String heading, desc;
      int icon;
      switch (notify_ID) {

         case 1:
            heading = "Today In Lunch";
            desc = getValue(context, (day) + String.valueOf(notify_ID));
            icon = R.drawable.lunch;
            sendNotification(heading, desc, context, notify_ID, icon);
            break;
         case 2:
            heading = "Today In Dinner";
            desc = getValue(context, (day) + String.valueOf(notify_ID));
            icon = R.drawable.dinner;
            sendNotification(heading, desc, context, notify_ID, icon);
            break;
         case 0:
            heading = "Today In Breakfast";
            desc = getValue(context, (day) + String.valueOf(notify_ID));
            icon = R.drawable.breakfast;
            sendNotification(heading, desc, context, notify_ID, icon);
            break;
         case 3:
            if (day != 1) {
               heading = "Today's Time Table";
               desc = getValue(context, String.valueOf(day));
               icon = R.drawable.timetable;
               sendNotification(heading, desc.replace("/", "\n"), context, notify_ID, icon);
            }
         case 4:
            heading = "Tomorrow Is Holiday";
            String key = (calendar.get(Calendar.DATE))+"/"+(calendar.get(Calendar.MONTH)+1);
            desc = getValue(context, key);
            Log.d("TAG",key);

            if (desc!=null) {
               icon = R.drawable.holiday;
               sendNotification(heading, desc, context, notify_ID, icon);
            }

            break;
         default:
            break;
      }
   }

   public void sendNotification(String title, String body, Context context, int notify_ID, int icon) {
      String channel_ID = "id_01";
      NotificationChannel channel = null;

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         channel = new NotificationChannel(channel_ID, "Events", NotificationManager.IMPORTANCE_HIGH);
      }

      Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
      NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
              .setSmallIcon(icon)
              .setContentTitle(title)
              .setContentText(body)
              .setPriority(Notification.PRIORITY_HIGH)
              .setAutoCancel(false)
              .setChannelId(channel_ID)
              .setSound(defaultSoundUri)
              .setStyle(new NotificationCompat.BigTextStyle().bigText(body));

      NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
         }
      }
      if (notificationManager != null) {
         notificationManager.notify(notify_ID, notificationBuilder.build());
      }

   }

   public String getValue(Context context, String key) {
      Gson gson = new Gson();
      SharedPreferences prefs = context.getSharedPreferences("notification", MODE_PRIVATE);
      //get from shared prefs
      String s = prefs.getString("hashString", "oopsDintWork");
      Log.d("wutmyalrm",s);
      java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {
      }.getType();
      HashMap<String, String> map ;
      try {
      	map = gson.fromJson(s, type);
      	return map.get(key);
		}catch (IllegalStateException | JsonSyntaxException | NullPointerException e){
      	Log.d("wutcatch",e.getMessage());
      	return "";
		}
      //use values
      
   }

   public void setAlarm(int id, Context context) {
      Date date = new Date();
      calendar.setTime(date);

      calendar.add(Calendar.DATE,1);

      Intent i = new Intent(context, MyAlarm.class);
      i.putExtra("Notification_ID",id);
      AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      PendingIntent pi = PendingIntent.getBroadcast(context, id, i, PendingIntent.FLAG_UPDATE_CURRENT);
      //setting the repeating alarm that will be fired every day
      if (am != null) {
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
         } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            am.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
         }else {
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
         }
      }
   }
}

