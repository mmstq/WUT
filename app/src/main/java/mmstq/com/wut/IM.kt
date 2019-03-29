package mmstq.com.wut


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import java.util.ArrayList

class IM : AppCompatActivity() {
   private val im = ArrayList<Im_Notice>()

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.fragment_blank)

      val rv = findViewById<RecyclerView>(R.id.rv)
      rv.layoutManager = LinearLayoutManager(this)
      rv.itemAnimator = DefaultItemAnimator()
      val adapterIm = AdapterIm(im)
      rv.adapter = adapterIm
      val dr = FirebaseDatabase.getInstance().reference.child("im")
      dr.addChildEventListener(object : ChildEventListener {

         override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val im_notice = dataSnapshot.getValue(Im_Notice::class.java)
            if (im_notice != null) {
               im.add(0, im_notice)
            }
            adapterIm.notifyDataSetChanged()
         }

         override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {}
         override fun onChildRemoved(dataSnapshot: DataSnapshot) {}
         override fun onChildMoved(dataSnapshot: DataSnapshot, s: String?) {}
         override fun onCancelled(databaseError: DatabaseError) {}

      })
   }


}
