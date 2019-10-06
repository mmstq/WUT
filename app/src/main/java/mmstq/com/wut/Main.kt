package mmstq.com.wut

import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.Window
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.main.*
import kotlinx.android.synthetic.main.mode_dialog.*
import java.util.concurrent.TimeUnit

class Main : AppCompatActivity() {

   private lateinit var sharedPreferences: SharedPreferences
   private lateinit var toolbar: androidx.appcompat.app.ActionBar


   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)


      sharedPreferences = Constant.sharedPreferenceInit(this@Main)
      val mode = sharedPreferences.getInt("Theme", AppCompatDelegate.MODE_NIGHT_NO)

      AppCompatDelegate.setDefaultNightMode(mode)

      val user = FirebaseAuth.getInstance().currentUser

      if (user == null) {
         val login = Login.newInstance()
         login.show(supportFragmentManager, "login")
         return
      } else {

         setContentView(R.layout.main)
         contact.text = Constant.getValue(this@Main, "phone")

      }

      Update.onCheck(this@Main)

      toolbar = supportActionBar!!
      val bottomNavigationView: BottomNavigationView = findViewById(R.id.navigationView)

      //Constant.updateNotification(this@Main)

      dBUpdater()

      Constant.dbRefresh(this@Main)

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
