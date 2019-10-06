package mmstq.com.wut

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.jetbrains.anko.doAsync


class DBRefresh(val context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
   override fun doWork(): Result {
      doAsync {

         Constant.dbRefresh(context)
      }
      return Result.success()
   }
}
