package mmstq.com.wut

import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.buy.*
import kotlinx.android.synthetic.main.buy.view.*

class CSurvey : Fragment() {
   private var activity: Activity? = null
   private var adapterRecycler: Adapter? = null
   private var pd: ProgressDialog? = null


   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      activity = getActivity()
   }

   override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
      val view = inflater.inflate(R.layout.buy, container, false)
      pd = ProgressDialog(activity)
      pd!!.setMessage("Loading Survey")
      pd!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
      pd!!.isIndeterminate = true
      pd!!.setCancelable(false)
      pd!!.progress = 0
      pd!!.show()
      val q = FirebaseFirestore.getInstance().collection("Survey").orderBy("surveyNo", Query.Direction.DESCENDING)

      val options = FirestoreRecyclerOptions.Builder<myData>()
              .setQuery(q, myData::class.java)
              .build()
      adapterRecycler = Adapter(options)
      view.listview!!.setHasFixedSize(true)
      view.listview!!.layoutManager = LinearLayoutManager(activity)
      view.listview!!.adapter = adapterRecycler
      adapterRecycler!!.notifyDataSetChanged()
      Handler().postDelayed({ pd!!.dismiss() }, 800)
      return view

   }

   private fun onRun() {

   }

   override fun onStart() {
      super.onStart()
      adapterRecycler!!.startListening()
   }

   override fun onStop() {
      super.onStop()
      adapterRecycler!!.stopListening()
   }
}

