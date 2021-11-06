package com.currency.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.currency.app.room.CurrecnyRoomModel
import com.currency.app.room.CurrencyDB
import com.currency.app.room.CurrencyRoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrecnyRoomViewMoel(application: Application) : AndroidViewModel(application) {

    private val repository: CurrencyRoomRepository

   // private lateinit var currencyRateRepsone:  LiveData<CurrecnyRoomModel>

    var currencyRateRepsone: LiveData<CurrecnyRoomModel>? = null

    init {
        val currencyDB = CurrencyDB.getDatabase(application).CurrencyDao()
        repository = CurrencyRoomRepository(currencyDB)
        //currencyRateRepsone = repository.getRate()
    }

    fun addCurrency(currecnyRoomModel: CurrecnyRoomModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addCurrency(currecnyRoomModel)

        }
    }

    fun deleteAllCurrency() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllCurrency()
        }
    }


    fun getRate(currecyCode: String) : LiveData<CurrecnyRoomModel>? {
        currencyRateRepsone = repository.getRate(currecyCode)
        return currencyRateRepsone
    }
}