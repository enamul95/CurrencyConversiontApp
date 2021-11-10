package com.currency.app.room

import androidx.lifecycle.LiveData
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

    @Query("SELECT * FROM currency order by conversionCurrency asc")
    fun getCurrencList(): LiveData<List<CurrecnyRoomModel>>

    @Query("SELECT * FROM currency WHERE conversionCurrency =:currecyCode")
    fun getRate(currecyCode: String): LiveData<CurrecnyRoomModel>

    @Query("SELECT COUNT(*) FROM CURRENCY")
    fun getRowCount(): LiveData<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPauseRefreshTime(vararg pauseRefreshRoomModel: PauseRefreshRoomModel)

    @Query("DELETE FROM pauseRefresh")
    suspend fun deletePauseTime()

    @Query("SELECT * FROM pauseRefresh")
    fun getPauseRefreshTime(): LiveData<PauseRefreshRoomModel>

}