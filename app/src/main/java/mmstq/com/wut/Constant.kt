package mmstq.com.wut

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.Log
import android.view.WindowManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat

class Constant {
   companion object {

      internal var mRealSizeWidth: Int = 0
      var cellNumber: String? = null
      var share_link: String? = null
      var isAdmin = false
      private var mDB: AppDatabase? = null
      private var dao: RawDao? = null

      @SuppressLint("SimpleDateFormat")
      fun setTime(ms: Long): String {
         return SimpleDateFormat("E, dd-MMM yy, hh:mm a").format(ms)
      }

      fun updateNotification(context: Context) {

         mDB = AppDatabase.getInstance(context)
         dao = mDB?.rawDao()

         val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
         val display = windowManager.defaultDisplay
         val outPoint = Point()
         if (Build.VERSION.SDK_INT >= 19) {
            // include navigation bar
            display.getRealSize(outPoint)
         } else {
            // exclude navigation bar
            display.getSize(outPoint)
         }
         mRealSizeWidth = if (outPoint.y > outPoint.x) {
            outPoint.x - 20
         } else {
            outPoint.y - 20
         }
      }

      @JvmStatic
      fun dbRefresh(){

         val rootRef = FirebaseDatabase.getInstance().reference.child("tt")
         val valueEventListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
               for (ds in dataSnapshot.children) {
                  val value = ds.child("value").getValue(String::class.java)
                  val key = ds.child("key").getValue(String::class.java)
                  val category = ds.child("category").getValue(String::class.java)
                  val record = Data(key!!, value, category)
                  if (value != null && category != null) with(dao) {
                     this?.addData(record)
                  }

               }
               AppDatabase.destroyInstance()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
         }
         rootRef.addListenerForSingleValueEvent(valueEventListener)
      }


   }
}
