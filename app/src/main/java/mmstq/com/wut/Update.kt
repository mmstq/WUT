package mmstq.com.wut

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.Window
import android.widget.Button
import android.widget.TextView

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

import java.text.MessageFormat
import java.util.*
import kotlin.collections.HashMap


class Update {

   companion object {
      private var version: Double = 0.toDouble()
      private var updVer: Double = 0.toDouble()
      private var updTitle: String? = null
      private var des: String? = null
      private var link: String? = null


      fun onCheck(context: Context) {
         val db = FirebaseFirestore.getInstance()
         val dr = db.collection("Update").document("Params")
         dr.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {

               val ds = task.result
               var isAvailable = false
               if (ds != null) {
                  isAvailable = ds.getBoolean("is_available")!!
                  updVer = java.lang.Double.parseDouble(ds.getString("version")!!)

                  val link = ds.getString("url")
                  Constant.share_link = link
                  updTitle = ds.getString("updTitle")
                  des = ds.getString("description")
                  des = if (des != null) des!!.replace("/", "\n") else ""
               }
               try {
                  val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                  version = java.lang.Double.parseDouble(pInfo.versionName)
               } catch (e: PackageManager.NameNotFoundException) {
                  e.printStackTrace()
               }

               if (isAvailable && version < updVer) {
                  val message1: TextView
                  val title: TextView
                  val ver: TextView
                  val pBtn: Button
                  val dialog = Dialog(context)
                  dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                  dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                  dialog.setCancelable(true)
                  dialog.setContentView(R.layout.update_dialog)

                  title = dialog.findViewById(R.id.title)
                  ver = dialog.findViewById(R.id.ver)
                  message1 = dialog.findViewById(R.id.message)
                  pBtn = dialog.findViewById(R.id.positiveBtn)

                  ver.text = MessageFormat.format("v{0}", ds!!.getString("version"))
                  title.text = updTitle
                  message1.text = des
                  pBtn.setOnClickListener {
                     val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                     context.startActivity(intent)
                     dialog.dismiss()
                  }
                  dialog.show()
               }
            }
         }
      }
   }
}
