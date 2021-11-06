package com.currency.app.room

import androidx.lifecycle.LiveData

class CurrencyRoomRepository(private val currencyDao: CurrencyDao) {

   // private lateinit var rate: LiveData<CurrecnyRoomModel>

    suspend fun addCurrency(currecnyRoomModel: CurrecnyRoomModel) {
        currencyDao.insertAll(currecnyRoomModel)
    }

    suspend fun deleteAllCurrency() {
        currencyDao.deleteAllCurrency()
    }

     fun getRate(currecyCode: String): LiveData<CurrecnyRoomModel>{
        return currencyDao.getRate(currecyCode)
        //return currencyDao.getRate(currecyCode)
    }
}