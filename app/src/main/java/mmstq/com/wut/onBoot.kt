package mmstq.com.wut

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences

import java.util.Calendar

class onBoot : BroadcastReceiver() {
   private var sp: SharedPreferences? = null
   private var s: Subs? = null
   private var calendar: Calendar? = null
   private var day: Int = 0
   private var month: Int = 0
   private var year: Int = 0

   override fun onReceive(context: Context, intent: Intent) {
      val action = intent.action
      if (action != null && action == "android.intent.action.BOOT_COMPLETED") {
         sp = context.getSharedPreferences("Phone", 0)
         calendar = Calendar.getInstance()
         day = calendar!!.get(Calendar.DATE)
         month = calendar!!.get(Calendar.MONTH)
         year = calendar!!.get(Calendar.YEAR)
         s = Subs()

         for (i in 0..4) {
            if (sp!!.getBoolean(i.toString(), false)) {
               addAlarm(i, context)
            }
         }
      }
   }

   private fun addAlarm(key: Int, context: Context) {
      calendar!!.timeInMillis = sp!!.getLong("0$key", 0)
      calendar!!.set(year, month, day)

      if (System.currentTimeMillis() > calendar!!.timeInMillis) {
         calendar!!.add(Calendar.DATE, 1)
      }

      s!!.setAlarm(calendar!!.timeInMillis, key, context, false)
   }

}
