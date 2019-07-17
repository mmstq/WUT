package mmstq.com.wut

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface RawDao {

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