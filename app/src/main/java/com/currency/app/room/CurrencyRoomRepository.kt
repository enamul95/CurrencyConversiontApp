package com.currency.app.room

class CurrencyRoomRepository(private val currencyDao: CurrencyDao) {

    suspend fun addCurrency(currecnyRoomModel: CurrecnyRoomModel) {
        currencyDao.insertAll(currecnyRoomModel)
    }

    suspend fun deleteAllCurrency() {
        currencyDao.deleteAllCurrency()
    }
}