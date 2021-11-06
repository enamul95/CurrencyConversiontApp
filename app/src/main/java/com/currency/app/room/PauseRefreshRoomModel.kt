package com.currency.app.room

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(
    tableName = "pauseRefresh", indices = arrayOf(
        Index(
            value = ["ID", "pasuseMillisecon"],
            unique = true
        )
    )
)

data class PauseRefreshRoomModel(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    @SerializedName("ID")
    var id: Int = 0,

    @ColumnInfo(name = "pasuseMillisecon")
    @SerializedName("pasuseMillisecon")
    var pasuseMillisecon: Long = 0,

    @ColumnInfo(name = "ispause")
    @SerializedName("ispause")
    var isPause: Boolean = false,
) : Parcelable