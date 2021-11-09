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
            value = ["ID", "currecyCode"],
            unique = true
        )
    )
)

data class CurrecnyRoomModel(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    @SerializedName("ID")
    var id: Int = 0,

    @ColumnInfo(name = "currecyCode")
    @SerializedName("currecyCode")
    var currecyCode: String = "",

    @ColumnInfo(name = "from")
    @SerializedName("from")
    var from: String = "",

    @ColumnInfo(name = "conversionCurrency")
    @SerializedName("conversionCurrency")
    var conversionCurrency: String = "",


    @ColumnInfo(name = "currecyRate")
    @SerializedName("currecyRate")
    var currecyRate: Double = 0.00

) : Parcelable