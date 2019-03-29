package mmstq.com.wut

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.migration.Migration
import android.content.Context

@Database(entities = [Data::class], version = 1)
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

   abstract fun rawDao(): rawDao

}