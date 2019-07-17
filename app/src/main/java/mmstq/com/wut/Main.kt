package mmstq.com.wut

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.im_dialog.*
import kotlinx.android.synthetic.main.main.*
import kotlinx.android.synthetic.main.mode_dialog.*
import kotlinx.android.synthetic.main.option_dl.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class Main : AppCompatActivity() {

   private lateinit var sharedPreferences: SharedPreferences
   private lateinit var dialog: Dialog
   private lateinit var toolbar: androidx.appcompat.app.ActionBar

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      sharedPreferences = getSharedPreferences("Phone", 0)
      val mode = sharedPreferences.getInt("Theme", AppCompatDelegate.MODE_NIGHT_NO)

      AppCompatDelegate.setDefaultNightMode(mode)

      Constant.cellNumber = sharedPreferences.getString("pNumber", "")
      val user = FirebaseAuth.getInstance().currentUser
      if (false) {
         val login = Login.newInstance()
         login.show(supportFragmentManager, "login")
         return
      } else {
         setContentView(R.layout.main)
         Update.onCheck(this@Main)
      }

      toolbar = supportActionBar!!
      val bottomNavigationView: BottomNavigationView = findViewById(R.id.navigationView)

      Constant.updateNotification(this@Main)
      Constant.dbRefresh()
      dBUpdater()

      val isAdmin = MyAlarm().getValue(
              this@Main, Constant.cellNumber!!)

      if (isAdmin != null && isAdmin != "") {
         Constant.isAdmin = true
         dialog = Dialog(this@Main)
         dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
         dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         dialog.setCancelable(true)
         dialog.setContentView(R.layout.option_dl)
      }

      surveyButton.setOnClickListener {


         startActivity(Intent(this@Main, Survey::class.java))

      }

      subs.setOnClickListener {
         startActivity(Intent(this@Main, Subs::class.java))
      }

      im.setOnClickListener {

         if (Constant.isAdmin) {

            dialog.view!!.setImageResource(R.drawable.viewmsg)
            dialog.send!!.setImageResource(R.drawable.sendmsg)
            dialog.send!!.setOnClickListener {
               dialog.dismiss()
               sendMsg(isAdmin!!)
            }
            dialog.view!!.setOnClickListener {
               dialog.dismiss()
               startActivity(Intent(this@Main, IM::class.java))
            }
            dialog.show()
         } else {
            startActivity(Intent(this@Main, IM::class.java))
         }
      }

      bottomNavigationView.setOnNavigationItemSelectedListener { item: MenuItem ->
         when (item.itemId) {

            R.id.logout -> {
               val builder = AlertDialog.Builder(this)
               builder.setTitle("Sign Out")
               builder.setPositiveButton("Yes") { dialog, _ ->

                  FirebaseAuth.getInstance().signOut()
                  val login = Login.newInstance()
                  dialog.dismiss()
                  login.show(supportFragmentManager, "login")
               }
               builder.setNegativeButton("No") { dialog, _ ->
                  dialog.dismiss()
               }
               builder.show()
            }
            R.id.theme -> {
               switchMode()
            }

            R.id.admin -> {
               val str =
                       if (Constant.isAdmin) getString(R.string.admin)
                       else getString(R.string.notAdmin)
               val sb: CustomSnackbar =
                       CustomSnackbar.make(this.window.decorView.findViewById(android.R.id.content),
                               2000)
               sb.setText(str)
               sb.show()


            }

            else -> {
               val intent = Intent()
               intent.action = Intent.ACTION_SEND
               intent.putExtra(Intent.EXTRA_TEXT,
                       resources.getString(R.string.description) +
                               "\nDownload Now: " +
                               Constant.share_link)
               intent.type = "text/plain"
               startActivity(intent)

            }

         }
         false
      }


   }

   private fun switchMode() {
      val mode = sharedPreferences.getInt("Theme", AppCompatDelegate.MODE_NIGHT_NO)
      val dialog = Dialog(this@Main)
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
      dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      dialog.setCancelable(true)
      dialog.setContentView(R.layout.mode_dialog)
      dialog.show()
      dialog.switchDN.isChecked = mode == 2
      dialog.switchDN.setOnCheckedChangeListener { _, isChecked ->

         if (isChecked) {
            sharedPreferences.edit().putInt("Theme", AppCompatDelegate.MODE_NIGHT_YES).apply()


         } else {
            sharedPreferences.edit().putInt("Theme", AppCompatDelegate.MODE_NIGHT_NO).apply()
         }
         Handler().postDelayed({
            dialog.cancel()
            finish()
            overridePendingTransition(0, 0)
            startActivity(intent)
            overridePendingTransition(0, 0)
         }, 500)


      }
   }

   @SuppressLint("SetTextI18n", "SimpleDateFormat")
   private fun sendMsg(senderName: String) {
      val dialog = Dialog(this@Main)
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
      dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      dialog.setCancelable(true)
      dialog.setContentView(R.layout.im_dialog)

      var userTargets = ""

      dialog.sendTo.setOnClickListener {
         val menu = PopupMenu(this, dialog.sendTo)
         menu.menuInflater.inflate(R.menu.im_menu, menu.menu)
         menu.setOnMenuItemClickListener { item ->

            userTargets = when (item.itemId) {

               R.id.all -> {
                  "mmstq.com.wut.im"
               }
               R.id.grp1 -> {
                  "mmstq.com.wut.im.grp1"
               }
               R.id.grp2 -> {
                  "mmstq.com.wut.im.grp2"
               }
               else -> {
                  "mmstq.com.wut.im.grp3"
               }
            }

            dialog.sendTo.text = "Send To : " + item.title
            true
         }
         menu.show()
      }

      dialog.voteBtn!!.setOnClickListener(View.OnClickListener {

         val headingText: String? = dialog.heading!!.text.toString()
         val descriptionText: String? = dialog.description!!.text.toString()

         if (headingText == "") {
            dialog.heading!!.error = "Field is necessary"
            dialog.heading!!.requestFocus()
            return@OnClickListener
         }

         if (descriptionText == "") {
            dialog.description!!.error = "Field is necessary"
            dialog.description!!.requestFocus()
            return@OnClickListener
         }

         if (userTargets == "") {
            dialog.sendTo.error = "Select Receiver"
            dialog.sendTo.requestFocus()
            return@OnClickListener
         }
         dialog.progress_bar!!.visibility = View.VISIBLE
         dialog.voteBtn!!.visibility = View.GONE


         val map = HashMap<String, Any>()
         val time = System.currentTimeMillis()
         map["name"] = headingText!!
         map["topic"] = userTargets
         map["time"] = time
         map["text"] = "$descriptionText\nBy: $senderName\nOn: " + SimpleDateFormat("E, hh:mm a")
                 .format(time)

         val fd = FirebaseDatabase
                 .getInstance()
                 .reference
                 .child("im")

         fd.push().setValue(map).addOnCompleteListener {
            dialog.progress_bar!!.visibility = View.GONE
            dialog.gifImageView!!.setImageResource(R.drawable.sent)
            Handler().postDelayed({ dialog.dismiss() }, 1850)

         }.addOnFailureListener { e ->
            dialog.progress_bar!!.visibility = View.GONE
            dialog.voteBtn!!.visibility = View.VISIBLE
            dialog.voteBtn!!.text = "Re-Try"
            Toast.makeText(this@Main, e.message, Toast.LENGTH_SHORT).show()
         }
      })
      dialog.show()
   }

   private fun dBUpdater() {

      val constraints = Constraints.Builder()
              .setRequiresCharging(true)
              .setRequiredNetworkType(NetworkType.CONNECTED)
              .build()

      val otr = PeriodicWorkRequest.Builder(
              DBRefresh::class.java,
              1, TimeUnit.DAYS)
              .setConstraints(constraints)
              .build()

      WorkManager.getInstance()
              .enqueueUniquePeriodicWork(
                      "RefreshDB",
                      ExistingPeriodicWorkPolicy.KEEP,
                      otr)
   }


}
