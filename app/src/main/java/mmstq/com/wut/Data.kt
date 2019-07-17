package mmstq.com.wut

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "database")

data class Data(

        @PrimaryKey @NonNull var key: String,
        @ColumnInfo(name = "value") var value: String?,
        @ColumnInfo(name = "category") var category: String?

)




