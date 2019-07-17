package mmstq.com.wut

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.jetbrains.anko.doAsync


class DBRefresh(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
   override fun doWork(): Result {
      doAsync {
         Constant.dbRefresh()
      }
      return Result.success()
   }
}
