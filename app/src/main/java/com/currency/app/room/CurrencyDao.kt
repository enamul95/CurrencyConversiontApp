package com.currency.app.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CurrencyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(vararg currecnyRoomModel: CurrecnyRoomModel)

    @Query("DELETE FROM currency")
    suspend fun deleteAllCurrency()
}