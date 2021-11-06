package com.currency.app.room

import androidx.lifecycle.LiveData

class CurrencyRoomRepository(private val currencyDao: CurrencyDao) {

    suspend fun addCurrency(currecnyRoomModel: CurrecnyRoomModel) {
        currencyDao.insertAll(currecnyRoomModel)
    }

    suspend fun deleteAllCurrency() {
        currencyDao.deleteAllCurrency()
    }

     fun getRate(currecyCode: String): LiveData<CurrecnyRoomModel>{
        return currencyDao.getRate(currecyCode)
    }

    fun getRowCount(): LiveData<Int>{
        return currencyDao.getRowCount()
    }

    suspend fun addRefressPauseTime(pauseRefreshRoomModel: PauseRefreshRoomModel) {
        currencyDao.insertPauseRefreshTime(pauseRefreshRoomModel)
    }

    suspend fun deletePauseTime() {
        currencyDao.deletePauseTime()
    }

    fun getPauseRefreshTime(): LiveData<PauseRefreshRoomModel>{
        return currencyDao.getPauseRefreshTime()
    }


}