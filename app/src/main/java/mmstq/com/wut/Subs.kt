package mmstq.com.wut

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.subs.*
import java.text.SimpleDateFormat
import java.util.*


class Subs : AppCompatActivity() {
   private var calendar: Calendar? = null
   private var sp: SharedPreferences? = null
   private var editor: SharedPreferences.Editor? = null
   private val foodBreakfast = 0
   private val foodLunch = 1
   private val foodDinner = 2
   private val timetableID = 3
   private val holidayID = 4
   private var context: Context? = null

   @SuppressLint("SetTextI18n")
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.subs)
      context = this@Subs
      /*
      return false*/

      calendar = Calendar.getInstance()
      sp = getSharedPreferences("Phone", 0)
      editor = sp!!.edit()

      checkSwitch()

      sTimeTable!!.setOnCheckedChangeListener { _, _ ->
         if (sTimeTable!!.isChecked) {
            Handler().postDelayed({ showTimePicker(timetableID, sTimeTable) }, 200)
         } else {
            cancelAlarm(timetableID)
         }
      }
      bFood!!.setOnCheckedChangeListener { _, _ ->
         if (bFood!!.isChecked) {
            Handler().postDelayed({ showTimePicker(foodBreakfast, bFood) }, 200)
         } else {
            cancelAlarm(foodBreakfast)
         }
      }
      lFood!!.setOnCheckedChangeListener { _, _ ->
         if (lFood!!.isChecked) {
            Handler().postDelayed({ showTimePicker(foodLunch, lFood) }, 200)
         } else {
            cancelAlarm(foodLunch)
         }
      }
      dFood!!.setOnCheckedChangeListener { _, _ ->
         if (dFood!!.isChecked) {
            Handler().postDelayed({ showTimePicker(foodDinner, dFood) }, 200)
         } else {
            cancelAlarm(foodDinner)
         }
      }
      im!!.setOnCheckedChangeListener { _, _ ->
         val state: String
         if (im!!.isChecked) {
            FirebaseMessaging.getInstance().subscribeToTopic("mmstq.com.wut.im")
            state = "On"
            imtext!!.text = "On"
         } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("mmstq.com.wut.im")
            state = "Off"
            imtext!!.text = "Off"

         }
         Toast.makeText(context!!, "Instant Messages : $state", Toast.LENGTH_SHORT).show()
         editor!!.putBoolean("im", im!!.isChecked)
         editor!!.putString("imtext", state)
         editor!!.apply()
      }
      sgrp1!!.setOnCheckedChangeListener { _, _ ->
         val state: String
         if (sgrp1!!.isChecked) {
            FirebaseMessaging.getInstance().subscribeToTopic("mmstq.com.wut.im.grp1")
            state = "On"
            textgp1!!.text = "On"
         } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("mmstq.com.wut.im.grp1")
            state = "Off"
            textgp1!!.text = "Off"

         }
         Toast.makeText(context!!, "Group I Messages : $state", Toast.LENGTH_SHORT).show()
         editor!!.putBoolean("gp1state", sgrp1!!.isChecked)
         editor!!.putString("tgp1", state)
         editor!!.apply()
      }
      sgrp3!!.setOnCheckedChangeListener { _, _ ->
         val state: String
         if (sgrp3!!.isChecked) {
            FirebaseMessaging.getInstance().subscribeToTopic("mmstq.com.wut.im.grp3")
            state = "On"
            textgp3!!.text = "On"
         } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("mmstq.com.wut.im.grp3")
            state = "Off"
            textgp3!!.text = "Off"

         }
         Toast.makeText(context!!, "Group III Messages : $state", Toast.LENGTH_SHORT).show()
         editor!!.putBoolean("gp3state", sgrp3!!.isChecked)
         editor!!.putString("tgp3", state)
         editor!!.apply()
      }
      sgrp2!!.setOnCheckedChangeListener { _, _ ->
         val state: String
         if (sgrp2!!.isChecked) {
            FirebaseMessaging.getInstance().subscribeToTopic("mmstq.com.wut.im.grp2")
            state = "On"
            textgp2!!.text = "On"
         } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("mmstq.com.wut.im.grp2")
            state = "Off"
            textgp2!!.text = "Off"

         }
         Toast.makeText(context!!, "Group II Messages : $state", Toast.LENGTH_SHORT).show()
         editor!!.putBoolean("gp2state", sgrp2!!.isChecked)
         editor!!.putString("tgp2", state)
         editor!!.apply()
      }
      survey!!.setOnCheckedChangeListener { _, _ ->
         val state: String
         if (survey!!.isChecked) {
            FirebaseMessaging.getInstance().subscribeToTopic("mmstq.com.wut.goSurvey")
            state = "On"
            surveytext!!.text = "On"
         } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("mmstq.com.wut.goSurvey")
            state = "Off"
            surveytext!!.text = "Off"
         }
         Toast.makeText(context!!, "WUT Messages : $state", Toast.LENGTH_SHORT).show()
         editor!!.putBoolean("goSurvey", survey!!.isChecked)
         editor!!.putString("surveytext", state)
         editor!!.apply()
      }
      holiday!!.setOnCheckedChangeListener { _, _ ->
         if (holiday!!.isChecked) {
            Handler().postDelayed({ showTimePicker(holidayID, holiday) }, 200)
         } else {
            cancelAlarm(holidayID)

         }
         editor!!.apply()
      }

   }

   fun show(v: View) {

      val calendar = Calendar.getInstance()
      val day = calendar.get(Calendar.DAY_OF_WEEK)
      val alarm = MyAlarm()
      val id = v.id

      val desc: String?

      desc = when (id) {
         R.id.bf -> {
            alarm.getValue(context!!, day.toString() + "0")
         }
         R.id.lunch -> {
            alarm.getValue(context!!, day.toString() + "1")
         }
         else -> {
            alarm.getValue(context!!, day.toString() + "2")
         }

      }
      val sb = CustomSnackbar
              .make(this@Subs.window.decorView.findViewById(android.R.id.content),
                      30000)
      sb.setText(desc.toString())
      sb.setAction("Hide", View.OnClickListener {
         sb.dismiss()
      })

      /*val view = sb.view
      val tv = view.findViewById<TextView>(android.support.design.R.id.snackbar_text)
      tv.setTextColor(Color.WHITE)
      tv.maxLines = 2
      tv.setBackgroundColor(resources.getColor(R.color.orange))
      tv.typeface = Typeface.MONOSPACE
      tv.textAlignment = View.TEXT_ALIGNMENT_CENTER*/
      sb.show()

   }

   fun setAlarm(timeInMillis: Long, id: Int, context: Context, run: Boolean) {

      val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
      val i = Intent(context, MyAlarm::class.java)
      //creating a new intent specifying the broadcast receiver
      i.putExtra("Notification_ID", id)
      //creating a pending intent using the intent
      val pi = PendingIntent.getBroadcast(context, id, i, PendingIntent.FLAG_UPDATE_CURRENT)
      //setting the repeating alarm that will be fired every day
      when {
         Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pi)
         Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT -> am.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pi)
         else -> am.set(AlarmManager.RTC_WAKEUP, timeInMillis, pi)
      }

      if (run) {
         editor!!.putBoolean(id.toString(), true)
         editor!!.putLong("0$id", timeInMillis)
         Toast.makeText(context, "Notification Set For " + formatTime(timeInMillis), Toast.LENGTH_SHORT).show()

         when (id) {

            0 -> {
               bfoodtext!!.text = formatTime(timeInMillis)
               editor!!.putLong("bfoodtext", timeInMillis)
            }
            1 -> {
               lfoodtext!!.text = formatTime(timeInMillis)
               editor!!.putLong("lfoodtext", timeInMillis)
            }
            2 -> {
               dfoodtext!!.text = formatTime(timeInMillis)
               editor!!.putLong("dfoodtext", timeInMillis)
            }
            3 -> {
               tttext!!.text = formatTime(timeInMillis)
               editor!!.putLong("tttext", timeInMillis)
            }
            4 -> {
               hdtext!!.text = formatTime(timeInMillis).replace("Everyday", "A Day Before Holiday")
               editor!!.putLong("hdt", timeInMillis)
            }
            else -> {
            }
         }
         editor!!.apply()
      }

   }

   private fun cancelAlarm(id: Int) {
      val i = Intent(this, MyAlarm::class.java)
      val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
      val pi = PendingIntent.getBroadcast(this, id, i, PendingIntent.FLAG_UPDATE_CURRENT)

      am.cancel(pi)
      editor!!.putBoolean(id.toString(), false)

      when (id) {
         0 -> {
            bfoodtext!!.setText(R.string.ns)
            editor!!.putLong("bfoodtext", 0)
         }
         1 -> {
            lfoodtext!!.setText(R.string.ns)
            editor!!.putLong("lfoodtext", 0)
         }
         2 -> {
            dfoodtext!!.setText(R.string.ns)
            editor!!.putLong("dfoodtext", 0)
         }
         3 -> {
            tttext!!.setText(R.string.ns)
            editor!!.putLong("tttext", 0)
         }
         4 -> {
            hdtext!!.setText(R.string.ns)
            editor!!.putLong("hdt", 0)
         }

         else -> {
         }
      }
      editor!!.apply()
   }

   private fun showTimePicker(id: Int, view: Switch?) {
      val timePicker: TimePicker
      val pBtn: Button
      val cancel: Button
      val dialog = Dialog(context!!)

      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
      dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      dialog.setCancelable(false)
      dialog.setContentView(R.layout.timepicker)

      timePicker = dialog.findViewById(R.id.tp)
      cancel = dialog.findViewById(R.id.negativeBtn)
      pBtn = dialog.findViewById(R.id.voteBtn)
      cancel.setOnClickListener {
         dialog.dismiss()
         view!!.isChecked = false
      }
      pBtn.setOnClickListener {
         val calendar = Calendar.getInstance()

         if (Build.VERSION.SDK_INT >= 23) {
            calendar.set(calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    timePicker.hour, timePicker.minute, 0)

            if (System.currentTimeMillis() > calendar.timeInMillis) {
               calendar.add(Calendar.DATE, 1)
            }

         } else {
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                    timePicker.currentHour, timePicker.currentMinute, 0)

            if (System.currentTimeMillis() > calendar.timeInMillis) {
               calendar.add(Calendar.DATE, 1)
            }
         }

         setAlarm(calendar.timeInMillis, id, context!!, true)
         dialog.dismiss()
      }
      dialog.show()

   }

   private fun checkSwitch() {
      bFood!!.isChecked = sp!!.getBoolean("0", false)
      lFood!!.isChecked = sp!!.getBoolean("1", false)
      dFood!!.isChecked = sp!!.getBoolean("2", false)
      im!!.isChecked = sp!!.getBoolean("im", false)
      survey!!.isChecked = sp!!.getBoolean("goSurvey", false)
      sgrp1!!.isChecked = sp!!.getBoolean("gp1state", false)
      sgrp2!!.isChecked = sp!!.getBoolean("gp2state", false)
      sgrp3!!.isChecked = sp!!.getBoolean("gp3state", false)
      sTimeTable!!.isChecked = sp!!.getBoolean("3", false)
      holiday!!.isChecked = sp!!.getBoolean("4", false)
      bfoodtext!!.text = formatTime(sp!!.getLong("bfoodtext", 0))
      lfoodtext!!.text = formatTime(sp!!.getLong("lfoodtext", 0))
      dfoodtext!!.text = formatTime(sp!!.getLong("dfoodtext", 0))
      tttext!!.text = formatTime(sp!!.getLong("tttext", 0))
      imtext!!.text = sp!!.getString("imtext", getString(R.string.ns))
      surveytext!!.text = sp!!.getString("surveytext", getString(R.string.ns))
      textgp1!!.text = sp!!.getString("tgp1", getString(R.string.ns))
      textgp2!!.text = sp!!.getString("tgp2", getString(R.string.ns))
      textgp3!!.text = sp!!.getString("tgp3", getString(R.string.ns))
      if (sp!!.getLong("hdt", 0) != 0L) {
         hdtext!!.text = formatTime(sp!!.getLong("hdt", 0)).replace("Everyday", "A Day Before Holiday")
      } else {
         hdtext!!.text = formatTime(sp!!.getLong("hdt", 0))
      }

   }

   @SuppressLint("SimpleDateFormat")
   fun formatTime(timeInMillis: Long): String {
      return if (timeInMillis != 0L) {
         calendar!!.timeInMillis = timeInMillis
         SimpleDateFormat("hh:mm a").format(calendar!!.time) + " | Everyday"
      } else {
         "Off"
      }
   }

}
