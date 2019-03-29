package mmstq.com.wut

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

import java.util.HashMap

class goSurvey : AppCompatActivity() {
   private val map = HashMap<String, Any>()
   private var byes_no = true
   private var bsay = true
   private var bwut = true
   private var bwut_opt = false
   private var ready = true
   private var like: ImageView? = null
   private var dislike: ImageView? = null
   private var say: ImageView? = null
   private var wut: EditText? = null
   private var like_text: TextView? = null
   private var dislike_text: TextView? = null
   private var say_text: TextView? = null
   private var wut_field: CheckBox? = null
   private var wut_nec: CheckBox? = null
   private var yes_no: CheckBox? = null
   private var inc_say: CheckBox? = null

   private val heading: String?
      get() {
         val heading = findViewById<EditText>(R.id.survey_heading)
         val head = heading.text.toString()

         if (head == "") {
            heading.error = "Enter Heading"
            heading.requestFocus()
            ready = false
            return null
         } else {
            ready = true
            return head
         }

      }

   private val description: String?
      get() {
         val description = findViewById<EditText>(R.id.survey_description)
         val des = description.text.toString()

         if (des == "") {
            description.error = "Enter Heading"
            description.requestFocus()
            ready = false
            return null
         } else {
            ready = true
            return des
         }
      }

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.survey)

      wut_field = findViewById(R.id.inc_field)
      wut_nec = findViewById(R.id.field_necessary)
      yes_no = findViewById(R.id.inc_yn)
      inc_say = findViewById(R.id.inc_say)
      like = findViewById(R.id.likeImg)
      dislike = findViewById(R.id.dislikeImg)
      wut = findViewById(R.id.survey_wut)
      say = findViewById(R.id.sayImg)
      like_text = findViewById(R.id.like_text)
      dislike_text = findViewById(R.id.dislike_text)
      say_text = findViewById(R.id.say_text)

      wut_field!!.setOnCheckedChangeListener { _, _ ->
         if (wut_field!!.isChecked) {

            findViewById<View>(R.id.survey_wut).visibility = View.INVISIBLE
            wut_nec!!.isEnabled = false
            bwut = false
            bwut_opt = false
            if (yes_no!!.isChecked) {
               yes_no!!.isChecked = false
            }
         } else {
            findViewById<View>(R.id.survey_wut).visibility = View.VISIBLE
            if (!inc_say!!.isChecked || !yes_no!!.isChecked) {
               wut_nec!!.isEnabled = true
            }
            bwut = true
         }
      }
      wut_nec!!.setOnCheckedChangeListener { _, _ ->
         if (wut_nec!!.isChecked) {
            bwut_opt = true
            wut!!.hint = "What You Think (Optional)"
         } else {
            wut!!.hint = "What You Think (Mandatory)"
            bwut_opt = false
         }
      }
      yes_no!!.setOnCheckedChangeListener { _, _ ->
         if (yes_no!!.isChecked) {
            if (wut_field!!.isChecked) {
               wut_field!!.isChecked = false
            }
            wut_nec!!.isChecked = false
            wut_nec!!.isEnabled = false
            bwut_opt = false
            byes_no = false
            dislike!!.setImageResource(R.drawable.dislike)
            dislike_text!!.setTextColor(resources.getColor(R.color.darkGrey))
            like!!.setImageResource(R.drawable.like)
            like_text!!.setTextColor(resources.getColor(R.color.darkGrey))
         } else {
            if (wut_field!!.isChecked) {
               wut_nec!!.isEnabled = false
            } else if (inc_say!!.isChecked) {
               wut_nec!!.isEnabled = true
            } else {
               wut_nec!!.isEnabled = true
            }

            byes_no = true
            dislike!!.setImageResource(R.drawable.dislike_red)
            dislike_text!!.setTextColor(resources.getColor(R.color.Red))
            like!!.setImageResource(R.drawable.like_green)
            like_text!!.setTextColor(resources.getColor(R.color.green))
         }
      }
      inc_say!!.setOnCheckedChangeListener { _, _ ->
         if (inc_say!!.isChecked) {
            if (yes_no!!.isChecked) {
               wut_nec!!.isChecked = false
               wut_nec!!.isEnabled = false
               bwut_opt = false
            }
            bsay = false
            say!!.setImageResource(R.drawable.can_say)
            say_text!!.setTextColor(resources.getColor(R.color.darkGrey))
         } else {
            if (!wut_field!!.isChecked) {
               wut_nec!!.isEnabled = true
            }
            if (yes_no!!.isChecked) {
               wut_nec!!.isEnabled = false
            }
            bsay = true
            say!!.setImageResource(R.drawable.cant_say)
            say_text!!.setTextColor(resources.getColor(R.color.orange))
         }
      }
      findViewById<View>(R.id.submit).setOnClickListener {
         val builder = ProgressDialog(this@goSurvey)
         builder.setMessage("Submitting...")
         builder.setProgressStyle(ProgressDialog.STYLE_SPINNER)
         builder.setCancelable(true)
         builder.progress = 0

         map.clear()
         map["likeDislike"] = byes_no
         map["say"] = bsay
         map["wut"] = bwut
         map["wutOptional"] = bwut_opt
         map["heading"] = heading!!
         map["open"] = true
         map["noLike"] = 0
         map["noDislike"] = 0
         map["noSay"] = 0
         map["noWut"] = 0
         map["admin"] = MyAlarm().getValue(this@goSurvey, Constant.cellNumber!!)!!
         map["description"] = description!!

         if (ready) {
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
               map["text"] = heading!!
               builder.dismiss()
               val fd = FirebaseDatabase.getInstance()
                       .reference.child("messages")
               fd.push().setValue(map)
            }.addOnFailureListener { e ->
               builder.dismiss()
               Toast.makeText(this@goSurvey, e.message, Toast.LENGTH_SHORT).show()
            }
         }
      }
   }
}
