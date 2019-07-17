package mmstq.com.wut


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlinx.android.synthetic.main.frag_blank.rv as rv1

class IM : AppCompatActivity() {
   private val im = ArrayList<Im_Notice>()
   private var rv:RecyclerView? = null

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      setContentView(R.layout.frag_blank)

      rv = findViewById(R.id.rv)


      rv!!.layoutManager = LinearLayoutManager(this)
      rv!!.itemAnimator = DefaultItemAnimator()
      val adapterIm = AdapterIm(im)
      rv!!.adapter = adapterIm
      val dr = FirebaseDatabase.getInstance().reference.child("im").orderByChild("time").limitToLast(40)
      dr.addChildEventListener(object : ChildEventListener {

         override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
            val im = dataSnapshot.getValue(Im_Notice::class.java)
            if (im != null) {
               this@IM.im.add(0, im)
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
