package mmstq.com.wut

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Constant {


   companion object {

      var list: Array<String>? = null
      var share_link: String? = null
      private var sharedPreferences: SharedPreferences? = null
      /*private var mDB: AppDatabase? = null
      private var dao: RawDao? = null*/

      fun sharedPreferenceInit(context: Context): SharedPreferences {

         return if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("DB", 0)
            sharedPreferences!!
         } else {
            sharedPreferences!!
         }
      }


      fun getValue(context: Context, key: String): String? {
         val sp = sharedPreferenceInit(context)
         return sp.getString(key, "")
         /*val mDB = AppDatabase.getInstance(context)
         return mDB?.rawDao()?.getByKey(key)?.value*/

      }


      @JvmStatic
      fun dbRefresh(context: Context) {

         val sp = sharedPreferenceInit(context).edit()
         val rootRef = FirebaseDatabase.getInstance().reference.child("tt")
         val valueEventListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
               for (ds in dataSnapshot.children) {
                  var value = ds.child("value").getValue(String::class.java)
                  val key = ds.child("key").getValue(String::class.java)

                  if (value != null) {
                     value = value.replace("/", ",")
                     value = value.replace(" : ", ",")
                     value = value.replace("-", " - ")
                     sp.putString(key, value)
                  }



                  /*val category = ds.child("category").getValue(String::class.java)
                  val record = Data(key!!, value, category)
                  if (value != null && category != null) with(dao) {
                     this?.addData(record)
                  }*/

               }
               sp.apply()
            }

            override fun onCancelled(databaseError: DatabaseError) {}
         }
         rootRef.addListenerForSingleValueEvent(valueEventListener)
      }


   }
}
