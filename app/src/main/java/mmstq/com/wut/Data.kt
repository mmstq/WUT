package mmstq.com.wut

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull


@Entity(tableName = "database")

data class Data(

        @PrimaryKey @NonNull var key: String,
        @ColumnInfo(name = "value") var value: String?,
        @ColumnInfo(name = "category") var category: String?

)




