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
    tableName = "currency", indices = arrayOf(
        Index(
            value = ["sl", "currecyCode"],
            unique = true
        )
    )
)

data class CurrecnyRoomModel(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sl")
    @SerializedName("sl")
    var sl: Int = 0,

    @ColumnInfo(name = "currecyCode")
    @SerializedName("currecyCode")
    var currecyCode: String = "",

    @ColumnInfo(name = "from")
    @SerializedName("from")
    var from: String = "",

    @ColumnInfo(name = "to")
    @SerializedName("to")
    var to: String = "",

    @ColumnInfo(name = "amount")
    @SerializedName("amount")
    var amount: Double = 0.00,

    @ColumnInfo(name = "currecyRate")
    @SerializedName("currecyRate")
    var currecyRate: Double = 0.00





): Parcelable