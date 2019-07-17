package mmstq.com.wut

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Data::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

   companion object {
      private var INSTANCE: AppDatabase? = null

      fun getInstance(context: Context): AppDatabase? {
         if (INSTANCE == null) {
            synchronized(AppDatabase::class) {
               INSTANCE = Room.databaseBuilder(context.applicationContext,
                       AppDatabase::class.java,"database")
                       .allowMainThreadQueries()
                       .build()
            }
         }
         return INSTANCE
      }

      fun destroyInstance() {
         INSTANCE = null
      }
   }

   abstract fun rawDao(): RawDao

}