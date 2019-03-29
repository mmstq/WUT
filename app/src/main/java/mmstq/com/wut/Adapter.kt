package mmstq.com.wut

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v4.widget.ImageViewCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.*
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.database.*
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.ads_dialog.*
import kotlinx.android.synthetic.main.response.*
import java.text.MessageFormat
import java.util.*


class Adapter
/**
 * Create a new RecyclerView WidgetAdapter that listens topsb a Firestore Query.  See [ ] for configuration options.
 *
 * @param options
 */
internal constructor(options: FirestoreRecyclerOptions<myData>) : FirestoreRecyclerAdapter<myData, Adapter.DataHolder>(options) {
   private var finalOpinion = 0
   private var finalOpstr = ""
   private var dr: DatabaseReference? = null
   private var context: Context? = null
   private val length = Constant.mRealSizeWidth
   internal lateinit var view: View

   override fun onBindViewHolder(holder: DataHolder, position: Int, model: myData) {

      val vote: Int
      if (model.isLikeDislike) {
         vote = model.noSay + model.noDislike + model.noLike
         if (vote / 74 >= 1) {
            view.layoutParams.width = length
         } else {
            view.layoutParams.width = vote * length / 74
         }
      } else {
         vote = model.noWut + model.noSay
         if (vote / 74 >= 1) {
            view.layoutParams.width = length
         } else {
            view.layoutParams.width = vote * length / 74
         }
      }
      if (!model.isOpen) {
         view.setBackgroundColor(context!!.resources.getColor(R.color.Red))
      }
      holder.textTop.text = model.heading
      holder.textTop.setOnClickListener {

         if (model.admin == MyAlarm().getValue(context!!, Constant.cellNumber!!)) {
            if (model.isOpen) adminOption(model, holder.textMid, R.menu.popup_menu1)
            else adminOption(model, holder.textMid, R.menu.popup_menu)
         } else {
            adminOption(model, holder.textMid, R.menu.popup_menu2)
         }
      }
      holder.textMid.text = model.description
      holder.textMid.setOnClickListener {

         if (model.admin == MyAlarm().getValue(context!!, Constant.cellNumber!!)) {
            if (model.isOpen) adminOption(model, holder.textMid, R.menu.popup_menu1)
            else adminOption(model, holder.textMid, R.menu.popup_menu)
         } else {
            adminOption(model, holder.textMid, R.menu.popup_menu2)
         }
      }
      holder.imageView.setOnClickListener { showDialog(model) }
   }

   inner class DataHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
      var textTop: TextView
      var textMid: TextView
      var imageView: ImageView

      init {
         view = itemView.findViewById(R.id.progress)
         textTop = itemView.findViewById(R.id.upperText)
         textMid = itemView.findViewById(R.id.lowertext)
         imageView = itemView.findViewById(R.id.book)
      }
   }

   override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DataHolder {
      context = viewGroup.context
      val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.cardview, viewGroup, false)
      return DataHolder(v)
   }


   @SuppressLint("SetTextI18n")
   private fun showDialog(model: myData) {

      val noLike = IntArray(1)
      val noDislike = IntArray(1)
      val noSay = IntArray(1)

      noDislike[0] = model.noDislike
      noSay[0] = model.noSay
      val like = booleanArrayOf(false)
      val dislike = booleanArrayOf(false)
      val say = booleanArrayOf(false)
      val wutOptional = booleanArrayOf(model.isWutOptional)
      val dialog = Dialog(context!!)
      dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
      dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
      dialog.setCancelable(true)
      dialog.setContentView(R.layout.ads_dialog)
      dialog.heading!!.text = model.heading
      dialog.desc!!.text = model.description
      dialog.date_text!!.text = Constant.setTime(model.surveyNo)
      dialog.admin_text!!.text = model.admin
      val color = ColorStateList.valueOf(ContextCompat.getColor(context!!, R.color.lightgrey))

      if (!model.isOpen) {
         dialog.voteBtn!!.isEnabled = false
         dialog.survey_wut!!.isEnabled = false
         dialog.voteBtn!!.text = "Survey Closed"
      }

      fun disable(t: TextView, img: ImageView) {
            img.isEnabled = false
            ImageViewCompat.setImageTintList(img, color)
            t.text = "Disabled"
            t.setTextColor(color)
         }

      if (!model.isLikeDislike) {
         disable(dialog.dislike_text!!, dialog.dislikeImg!!)
         disable(dialog.like_text!!, dialog.likeImg!!)
      }

      if (!model.isSay) {
         disable(dialog.say_text!!, dialog.sayImg!!)
      }

      if (model.isWut) {
         dialog.survey_wut!!.visibility = View.VISIBLE
         if (model.isWutOptional) {
            dialog.survey_wut!!.hint = "Optional"
         }
      }


      dialog.voteBtn!!.setOnClickListener(View.OnClickListener {
         val map = HashMap<String, Any>()

         if (like[0]) {
            finalOpinion = noLike[0]
            finalOpstr = "noLike"
            if (model.isWut && !wutOptional[0] && dialog.survey_wut!!.text.toString() == "") {
               dialog.survey_wut!!.error = "Field Is Necessary"
               dialog.survey_wut!!.requestFocus()
               return@OnClickListener

            }
         } else if (dislike[0]) {
            finalOpinion = noDislike[0]
            finalOpstr = "noDislike"
            if (model.isWut && !wutOptional[0] && dialog.survey_wut!!.text.toString() == "") {
               dialog.survey_wut!!.error = "Field Is Necessary"
               dialog.survey_wut!!.requestFocus()
               return@OnClickListener
            }

         } else if (!model.isLikeDislike) {
            if (!say[0]) {
               dialog.survey_wut!!.hint = "Necessary"
               if (dialog.survey_wut!!.text.toString() == "") {
                  dialog.survey_wut!!.error = "Field Is Necessary"
                  dialog.survey_wut!!.requestFocus()
                  return@OnClickListener
               }
               finalOpinion = model.noWut + 1
               finalOpstr = "noWut"
            }

         } else if (say[0]) {
            finalOpinion = noSay[0]
            finalOpstr = "noSay"

         } else {
            Toast.makeText(context, "Select One Opinion", Toast.LENGTH_SHORT).show()
            return@OnClickListener
         }


         dialog.pb!!.visibility = View.VISIBLE
         dialog.voteBtn!!.visibility = View.GONE

         map["phone"] = Constant.cellNumber!!
         map["wut"] = dialog.survey_wut!!.text.toString()

         dr = FirebaseDatabase.getInstance().reference.child("Feedback").child(model.surveyNo.toString())
         dr!!.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
               if (dataSnapshot.exists()) {
                  dialog.pb!!.visibility = View.GONE
                  dialog.voteBtn!!.visibility = View.VISIBLE
                  dialog.voteBtn!!.isEnabled = false
                  dialog.survey_wut!!.setText("")
                  dialog.survey_wut!!.isEnabled = false
                  dialog.voteBtn!!.text = "Voted Already"

               } else {
                  dr!!.child(Constant.cellNumber!!).setValue(map).addOnCompleteListener {
                     val query = FirebaseFirestore.getInstance()
                             .collection("Survey")
                             .document(model.surveyNo.toString())
                     query.update(finalOpstr, finalOpinion).addOnSuccessListener {
                        dialog.pb!!.visibility = View.GONE
                        dialog.voteBtn!!.visibility = View.VISIBLE
                        dialog.voteBtn!!.text = "Done"
                        dialog.voteBtn!!.isEnabled = false
                        dialog.survey_wut!!.setText("")
                        dialog.dislike_text!!.text = MessageFormat.format("{0}%", model.noDislike)
                        dialog.like_text!!.text = MessageFormat.format("{0}%", model.noLike)
                        dialog.say_text!!.text = MessageFormat.format("{0}%", model.noSay)

                        android.os.Handler().postDelayed({ dialog.dismiss() }, 2500)
                     }.addOnFailureListener { e ->
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                        dialog.pb!!.visibility = View.GONE
                        dialog.voteBtn!!.visibility = View.VISIBLE
                        dialog.voteBtn!!.text = "Submit Again"
                     }.addOnFailureListener { e ->
                        dialog.pb!!.visibility = View.GONE
                        dialog.voteBtn!!.visibility = View.VISIBLE
                        dialog.voteBtn!!.text = "Submit Again"
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                     }
                  }
               }
            }

            override fun onCancelled(databaseError: DatabaseError) {
               Toast.makeText(context, databaseError.message, Toast.LENGTH_SHORT).show()
            }
         })
      })

      dialog.sayImg!!.setOnClickListener { v ->
         dialog.sayImg!!.setImageResource(R.drawable.cant_say)
         dialog.say_text!!.setTextColor(v.resources.getColor(R.color.orange))
         dialog.dislike_text!!.setTextColor(v.resources.getColor(R.color.darkGrey))
         dialog.like_text!!.setTextColor(v.resources.getColor(R.color.darkGrey))
         dialog.dislikeImg!!.setImageResource(R.drawable.dislike)
         dialog.likeImg!!.setImageResource(R.drawable.like)
         like[0] = false
         dislike[0] = false
         say[0] = true
         dialog.survey_wut!!.setText("")
         dialog.survey_wut!!.visibility = View.GONE
         noSay[0] = model.noSay + 1
      }
      dialog.dislikeImg!!.setOnClickListener { v ->
         dialog.dislikeImg!!.setImageResource(R.drawable.dislike_red)
         dialog.dislike_text!!.setTextColor(v.resources.getColor(R.color.Red))
         dialog.like_text!!.setTextColor(v.resources.getColor(R.color.darkGrey))
         dialog.say_text!!.setTextColor(v.resources.getColor(R.color.darkGrey))
         dialog.sayImg!!.setImageResource(R.drawable.can_say)
         dialog.likeImg!!.setImageResource(R.drawable.like)
         like[0] = false
         dislike[0] = true
         say[0] = false
         if (model.isWut) {
            dialog.survey_wut!!.visibility = View.VISIBLE
            if (model.isWutOptional) {
               dialog.survey_wut!!.hint = "What You Think (Optional)"
            }
         }
         noDislike[0] = model.noDislike + 1
      }
      dialog.likeImg!!.setOnClickListener { v ->
         dialog.likeImg!!.setImageResource(R.drawable.like_green)
         dialog.like_text!!.setTextColor(v.resources.getColor(R.color.green))
         dialog.say_text!!.setTextColor(v.resources.getColor(R.color.darkGrey))
         dialog.dislike_text!!.setTextColor(v.resources.getColor(R.color.darkGrey))
         dialog.sayImg!!.setImageResource(R.drawable.can_say)
         dialog.dislikeImg!!.setImageResource(R.drawable.dislike)
         like[0] = true
         dislike[0] = false
         say[0] = false
         if (model.isWut) {
            dialog.survey_wut!!.visibility = View.VISIBLE
            if (model.isWutOptional) {
               dialog.survey_wut!!.hint = "What You Think (Optional)"
            }
         }
         noLike[0] = model.noLike + 1
      }
      dialog.show()

   }

   private fun adminOption(model: myData, textTop: TextView, menuToInflate: Int) {
      val menu = PopupMenu(context, textTop)
      menu.menuInflater.inflate(menuToInflate, menu.menu)
      menu.setOnMenuItemClickListener { item ->
         val query = FirebaseFirestore.getInstance()
                 .collection("Survey")
                 .document(model.surveyNo.toString())
         when (item.itemId) {
            R.id.delete -> {

               query.update("likeDislike", FieldValue.delete())
               query.update("say", FieldValue.delete())
               query.update("wut", FieldValue.delete())
               query.update("wutOptional", FieldValue.delete())
               query.update("heading", FieldValue.delete())
               query.update("open", FieldValue.delete())
               query.update("noLike", FieldValue.delete())
               query.update("noDislike", FieldValue.delete())
               query.update("noSay", FieldValue.delete())
               query.update("description", FieldValue.delete())
               query.update("surveyNo", FieldValue.delete())
               query.update("admin", FieldValue.delete())
               query.update("noWut", FieldValue.delete())
            }
            R.id.close -> query.update("open", false)
            R.id.open -> query.update("open", true)
            R.id.response -> response(model.surveyNo.toString())
            else -> {
            }
         }
         true
      }
      menu.show()
   }

   private fun response(node: String) {
      val values = ArrayList<String>()
      val rootRef = FirebaseDatabase.getInstance().reference
      val uidRef = rootRef.child("Feedback").child(node)
      val valueEventListener = object : ValueEventListener {

         override fun onDataChange(dataSnapshot: DataSnapshot) {
            for (ds in dataSnapshot.children) {
               var value = ds.child("wut").getValue(String::class.java)
               if (value != "" && value != null) {
                  value = ds.child("phone").getValue(String::class.java) + ": "+ value
                  values.add(value)
               }
            }
            val arrayAdapter = ArrayAdapter(context!!, android.R.layout.simple_list_item_1, values)

            val dialog = Dialog(context!!)
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.setCancelable(true)
            dialog.setContentView(R.layout.response)

            if (values.isEmpty()) {
               val t = dialog.findViewById<TextView>(R.id.text)
               t.text = "No Response"
            }

            dialog.listview!!.adapter = arrayAdapter
            dialog.show()

         }

         override fun onCancelled(databaseError: DatabaseError) {}
      }
      uidRef.addListenerForSingleValueEvent(valueEventListener)

   }

}

