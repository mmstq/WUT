package mmstq.com.wut

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query

@Dao
interface rawDao {

   @Query("SELECT * from `database`")
   fun getAll(): List<Data>

   @Query("SELECT * FROM `database` WHERE `key` LIKE :key LIMIT 1")
   fun getByKey(key: String): Data

   @Query("SELECT * FROM `database` WHERE `value` LIKE :value LIMIT 1")
   fun getByValue(value: String): Data

   @Query("SELECT * FROM `database` WHERE `key` LIKE :key & `category` LIKE :category LIMIT 1")
   fun getByKeyAndCategory(key: String, category: String): Data

   @Query("DELETE FROM `database`")
   fun deleteTable()

   @Insert(onConflict = REPLACE)
   fun addData(vararg data: Data)

   @Delete
   fun delete(data: Data)

}