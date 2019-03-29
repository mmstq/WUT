package mmstq.com.wut

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import kotlinx.android.synthetic.main.log_in.view.*

class Login : DialogFragment() {

   private var phone: EditText? = null

   private var onClickListener: View.OnClickListener = View.OnClickListener { v ->
      when (v.id) {

         R.id.github -> {
            val urlString = "https://github.com/mmstq/WUT"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.setPackage("com.android.chrome")
            try {
               startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
               intent.setPackage(null)
               startActivity(intent)
            }

         }
         R.id.exit -> System.exit(1)
         else -> {
            val pNumber = phone!!.text.toString().trim { it <= ' ' }
            if (pNumber.isEmpty() || pNumber.length < 10) {
               phone!!.error = "Number Is Required"
               phone!!.requestFocus()
               return@OnClickListener
            }
            val phoneNo = "+91$pNumber"
            Constant.cellNumber = pNumber
            val editor = context!!.getSharedPreferences("Phone", 0).edit()
            editor.putString("Phone_No", phoneNo)
            editor.putString("pNumber", pNumber)
            editor.apply()
            phone!!.setText("")
            val b = VerifyOTP.newInstance()
            if (fragmentManager != null) {
               b.show(fragmentManager,"verify")
            }
         }
      }
   }

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      val v = inflater.inflate(R.layout.log_in, container, false)
      phone = v.findViewById(R.id.phone_number)
      v.login.setOnClickListener(onClickListener)
      v.github.setOnClickListener(onClickListener)
      v.exit.setOnClickListener(onClickListener)
      return v
   }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      isCancelable = false
   }

   override fun getTheme(): Int {
      return R.style.FullScreenDialog
   }

   companion object {

      internal fun newInstance(): Login {
         return Login()
      }
   }
}
