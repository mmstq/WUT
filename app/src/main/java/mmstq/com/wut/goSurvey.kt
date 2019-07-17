package mmstq.com.wut

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.survey.*
import kotlinx.android.synthetic.main.survey.view.*
import java.util.*

class GoSurvey : Fragment() {
   private val map = HashMap<String, Any>()
   private var isYesNo = true
   private var isSay = true
   private var isWut = true
   private var isWutOptional = false
   private var activity: Activity? = null
   private var isReady = true

   private val heading: String
      get() {

         val head:String? = survey_heading!!.text.toString()
         return if (head == null || head =="") {
            survey_heading.error = "Enter Heading"
            survey_heading.requestFocus()
            isReady = false
            ""
         } else {
            isReady = true
            head
         }
      }

   private val description: String
      get() {

         val des:String? = survey_description!!.text.toString()
         return if (des == "" || des == null) {
            survey_description!!.error = "Enter Description"
            survey_description!!.requestFocus()
            isReady = false
            ""
         } else {
            isReady = true
            des
         }
      }

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      val view = inflater.inflate(R.layout.survey,container,false)
      view.inc_field!!.setOnCheckedChangeListener { _, _ ->
         if (inc_field!!.isChecked) {

            survey_wut!!.visibility = View.INVISIBLE
            field_necessary!!.isEnabled = false
            isWut = false
            isWutOptional = false
            if (inc_yn!!.isChecked) {
               inc_yn!!.isChecked = false
            }
         } else {
            survey_wut!!.visibility = View.VISIBLE
            if (!inc_say!!.isChecked || !inc_yn!!.isChecked) {
               field_necessary!!.isEnabled = true
            }
            isWut = true
         }
      }

      view.field_necessary!!.setOnCheckedChangeListener { _, _ ->
         if (field_necessary!!.isChecked) {
            isWutOptional = true
            survey_wut!!.hint = "This Field Will Be Optional"
         } else {
            survey_wut!!.hint = "This Field Will Be Mandatory"
            isWutOptional = false
         }
      }

      view.inc_yn!!.setOnCheckedChangeListener { _, _ ->
         if (inc_yn!!.isChecked) {
            if (inc_field!!.isChecked) {
               inc_field!!.isChecked = false
            }
            field_necessary!!.isChecked = false
            field_necessary!!.isEnabled = false
            isWutOptional = false
            isYesNo = false
            dislikeImg!!.setImageResource(R.drawable.dislike)
            dislike_text!!.setTextColor(ContextCompat.getColor(context!!, R.color.darkGrey))
            likeImg!!.setImageResource(R.drawable.like)
            like_text!!.setTextColor(ContextCompat.getColor(context!!, R.color.darkGrey))
         } else {
            when {
               inc_field!!.isChecked -> field_necessary!!.isEnabled = false
               inc_say!!.isChecked -> field_necessary!!.isEnabled = true
               else -> field_necessary!!.isEnabled = true
            }

            isYesNo = true
            dislikeImg!!.setImageResource(R.drawable.dislike_red)
            dislike_text!!.setTextColor(ContextCompat.getColor(context!!, R.color.Red))
            likeImg!!.setImageResource(R.drawable.like_green)
            like_text!!.setTextColor(ContextCompat.getColor(context!!, R.color.green))
         }
      }
      view.inc_say!!.setOnCheckedChangeListener { _, _ ->
         if (inc_say!!.isChecked) {
            if (inc_yn!!.isChecked) {
               field_necessary!!.isChecked = false
               field_necessary!!.isEnabled = false
               isWutOptional = false
            }
            isSay = false
            sayImg!!.setImageResource(R.drawable.can_say)
            say_text!!.setTextColor(ContextCompat.getColor(context!!, R.color.darkGrey))
         } else {
            if (!inc_field!!.isChecked) {
               field_necessary!!.isEnabled = true
            }
            if (inc_yn!!.isChecked) {
               field_necessary!!.isEnabled = false
            }
            isSay = true
            sayImg!!.setImageResource(R.drawable.cant_say)
            say_text!!.setTextColor(ContextCompat.getColor(context!!, R.color.orange))
         }
      }
      view.submit!!.setOnClickListener {
         val builder = ProgressDialog(context!!)
         builder.setMessage("Submitting...")
         builder.setProgressStyle(ProgressDialog.STYLE_SPINNER)
         builder.setCancelable(true)
         builder.progress = 0

         map.clear()
         map["likeDislike"] = isYesNo
         map["say"] = isSay
         map["wut"] = isWut
         map["wutOptional"] = isWutOptional
         map["heading"] = heading
         map["open"] = true
         map["noLike"] = 0
         map["noDislike"] = 0
         map["noSay"] = 0
         map["noWut"] = 0
         map["admin"] = MyAlarm().getValue(context!!, Constant.cellNumber!!)!!
         map["description"] = description

         if (isReady) {
            val timestamp = System.currentTimeMillis()
            map["surveyNo"] = timestamp
            builder.show()
            val cr = FirebaseFirestore.getInstance()
                    .collection("Survey")
                    .document(timestamp.toString())

            cr.set(map).addOnCompleteListener {
               val map = HashMap<String, String>()
               map["name"] = "What You Think..?"
               map["topic"] = "mmstq.com.wut.goSurvey"
               map["text"] = heading
               builder.dismiss()
               val fd = FirebaseDatabase.getInstance()
                       .reference.child("messages")
               fd.push().setValue(map)
            }.addOnFailureListener { e ->
               builder.dismiss()
               Toast.makeText(context!!, e.message, Toast.LENGTH_SHORT).show()
            }
         }
      }
      return view
   }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      activity = getActivity()
      

   }
}
