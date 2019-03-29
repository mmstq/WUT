package mmstq.com.wut

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.work.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.im_dialog.*
import kotlinx.android.synthetic.main.main.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class Main : AppCompatActivity() {

   override fun onOptionsItemSelected(item: MenuItem?): Boolean {
      val id: Int = item!!.itemId
      when (id) {
         R.id.logout -> {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Sign Out")
            builder.setPositiveButton("Yes") { dialog, _ ->

               FirebaseAuth.getInstance().signOut()
               val login = Login.newInstance()
               login.show(supportFragmentManager, "login")
               dialog.dismiss()
            }
            builder.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            builder.show()
         }
         else -> {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, resources.getString(R.string.description) +
                    "\nDownload Now: " +
                    Constant.share_link)
            intent.type = "text/plain"
            startActivity(intent)

         }
      }
      return super.onOptionsItemSelected(item)
   }

   override fun onCreateOptionsMenu(menu: Menu?): Boolean {
      menuInflater.inflate(R.menu.main_menu, menu)
      return super.onCreateOptionsMenu(menu)
   }

   @SuppressLint("SimpleDateFormat", "SetTextI18n")
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      Constant.cellNumber = getSharedPreferences("Phone", 0)
              .getString("pNumber", "")

      if (FirebaseAuth.getInstance().currentUser != null) {
         val login = Login.newInstance()
         login.show(supportFragmentManager, "login")
         return
      } else {
         setContentView(R.layout.main)
         Update.onCheck(this@Main)
      }

      admin.setOnClickListener {
         val sb = Snackbar.make(this.window.decorView.findViewById(android.R.id.content),
                 getString(R.string.admin), Snackbar.LENGTH_SHORT)
         val v = sb.view
         val tv = v.findViewById<TextView>(android.support.design.R.id.snackbar_text)
         tv.setTextColor(Color.WHITE)
         tv.maxLines = 1
         tv.typeface = Typeface.DEFAULT_BOLD; Typeface.SERIF
         if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            SnackbarHelper.configSnackbar(this@Main, sb, true)
         }

         sb.show()
      }
      Constant.updateNotification(this@Main)
      Constant.dbRefresh()

      val constraints = Constraints.Builder()
              .setRequiresCharging(true)
              .setRequiredNetworkType(NetworkType.CONNECTED)
              .build()

      val otr = PeriodicWorkRequest.Builder(DBRefresh::class.java, 1, TimeUnit.DAYS)
              .setConstraints(constraints)
              .build()

      WorkManager.getInstance().enqueueUniquePeriodicWork("RefreshDB", ExistingPeriodicWorkPolicy.KEEP, otr)

      val myAlarm = MyAlarm()

      val value = myAlarm.getValue(this@Main, Constant.cellNumber!!)

      if (value != null && value != "") {
         admin.visibility = View.VISIBLE
         Constant.isAdmin = true
         surveyButton.isEnabled = true
         im.isEnabled = true
      }

      surveyButton.setOnClickListener {
         startActivity(Intent(this@Main, goSurvey::class.java))
      }
      subs.setOnClickListener {
         startActivity(Intent(this@Main, Subs::class.java))
      }
      (view_survey).setOnClickListener {
         startActivity(Intent(this@Main, cSurvey::class.java))
      }
      im.setOnClickListener(View.OnClickListener {

         val dialog = Dialog(this@Main)
         dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
         dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
         dialog.setCancelable(true)
         dialog.setContentView(R.layout.im_dialog)

         var topic = ""

         dialog.sendTo.setOnClickListener {
            val menu = PopupMenu(this, dialog.sendTo)
            menu.menuInflater.inflate(R.menu.im_menu, menu.menu)
            menu.setOnMenuItemClickListener { item ->

               topic = when (item.itemId) {

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

            val head: String? = dialog.heading!!.text.toString()
            val desc: String? = dialog.description!!.text.toString()

            if (head == "") {
               dialog.heading!!.error = "Field is necessary"
               dialog.heading!!.requestFocus()
               return@OnClickListener
            }

            if (desc == "") {
               dialog.description!!.error = "Field is necessary"
               dialog.description!!.requestFocus()
               return@OnClickListener
            }

            if (topic == "") {
               dialog.sendTo.error = "Select Receiver"
               dialog.sendTo.requestFocus()
               return@OnClickListener
            }

            dialog.voteBtn!!.visibility = View.GONE
            dialog.progress_bar!!.visibility = View.VISIBLE

            val map = HashMap<String, Any>()
            map["name"] = head!!
            map["topic"] = topic
            map["text"] = "$desc\nBy: $value\nOn: " + SimpleDateFormat("E, hh:mm a")
                    .format(System.currentTimeMillis())

            val fd = FirebaseDatabase.getInstance().reference.child("im")

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
      })
   }


}
