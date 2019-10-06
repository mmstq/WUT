package mmstq.com.wut

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.log_in.view.*
import java.util.concurrent.TimeUnit

class VerifyOTP : DialogFragment() {
   private var phone: EditText? = null
   private var mAuth: FirebaseAuth? = null
   private var sp: SharedPreferences? = null
   private var pb: ProgressBar? = null
   private var codeVerify: String? = null

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
         R.id.exit -> {
            val dialog = Login.newInstance()
            if (fragmentManager != null) {
               dialog.show(fragmentManager!!, "login")
            }
         }
         else -> {
            val code = phone!!.text.toString().trim { it <= ' ' }
            if (code.isEmpty() || code.length < 6) {
               phone!!.error = "Enter Code..."
               phone!!.requestFocus()
               return@OnClickListener
            } else {
               if (code == codeVerify) {
                  val credential = PhoneAuthProvider.getCredential(verificationID!!, code)
                  signInWithCredential(credential)
               } else {
                  phone!!.error = "Incorrect OTP..!"
                  phone!!.requestFocus()
               }

            }
         }
      }
   }

   private val mCallBack = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
      override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
         super.onCodeSent(p0, p1)
         verificationID = p0
      }

      override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
         codeVerify = phoneAuthCredential.smsCode
         if (codeVerify != null) {
            phone!!.setText(codeVerify)
            val credential = PhoneAuthProvider.getCredential(verificationID!!, codeVerify!!)
            signInWithCredential(credential)
         }
      }

      override fun onVerificationFailed(e: FirebaseException) {
         Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
      }
   }

   @SuppressLint("SetTextI18n")
   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      val v = inflater.inflate(R.layout.log_in, container, false)
      pb = v.findViewById(R.id.pb)
      pb!!.visibility = View.VISIBLE
      phone = v.findViewById(R.id.phone_number)
      phone!!.hint = "Waiting For OTP..."

      v.login.text = "Submit"
      isCancelable = false
      v.login.setOnClickListener(onClickListener)
      v.github.setOnClickListener(onClickListener)

      v.exit.text = "Back"
      v.exit.setOnClickListener(onClickListener)
      return v
   }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      mAuth = FirebaseAuth.getInstance()
      sp = context!!.getSharedPreferences("Phone", 0)
      sendVerificationCode(sp!!.getString("Phone_No", ""))
   }

   override fun getTheme(): Int {
      return R.style.FullScreenDialog
   }

   private fun signInWithCredential(credential: PhoneAuthCredential) {
      mAuth!!.signInWithCredential(credential).addOnCompleteListener {
         val intent = Intent(context, Main::class.java)
         intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
         pb!!.visibility = View.GONE
         startActivity(intent)
      }
   }

   private fun sendVerificationCode(number: String?) {
      PhoneAuthProvider.getInstance().verifyPhoneNumber(
              number!!, 60, TimeUnit.SECONDS, TaskExecutors.MAIN_THREAD, mCallBack
      )
   }

   companion object {
      private var verificationID: String? = null

      internal fun newInstance(): VerifyOTP {
         return VerifyOTP()
      }
   }
}
