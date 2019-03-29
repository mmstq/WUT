package mmstq.com.wut

import android.app.Activity
import android.app.ProgressDialog
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class cSurvey : AppCompatActivity() {
   private var activity: Activity? = null
   private var adapterRecycler: Adapter? = null
   private var rv: RecyclerView? = null
   private var pd: ProgressDialog? = null


   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.buy)

      activity = this@cSurvey
      pd = ProgressDialog(activity)
      pd!!.setMessage("Loading Survey")
      pd!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
      pd!!.isIndeterminate = true
      pd!!.setCancelable(false)
      pd!!.progress = 0

      rv = findViewById(R.id.listview)

      onRun()

   }

   private fun onRun() {
      pd!!.show()
      val q = FirebaseFirestore.getInstance().collection("Survey").orderBy("surveyNo", Query.Direction.DESCENDING)

      val options = FirestoreRecyclerOptions.Builder<myData>()
              .setQuery(q, myData::class.java)
              .build()
      adapterRecycler = Adapter(options)
      rv!!.setHasFixedSize(true)
      rv!!.layoutManager = LinearLayoutManager(activity)
      rv!!.adapter = adapterRecycler
      adapterRecycler!!.notifyDataSetChanged()
      Handler().postDelayed({ pd!!.dismiss() }, 800)
   }

   public override fun onStart() {
      super.onStart()
      adapterRecycler!!.startListening()
   }

   public override fun onStop() {
      super.onStop()
      adapterRecycler!!.stopListening()
   }
}

