package com.currency.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.currency.app.room.CurrecnyRoomModel
import com.currency.app.room.CurrencyDB
import com.currency.app.room.CurrencyRoomRepository
import com.currency.app.room.PauseRefreshRoomModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CurrecnyRoomViewMoel(application: Application) : AndroidViewModel(application) {

    private val repository: CurrencyRoomRepository
    var currencyRateRepsone: LiveData<CurrecnyRoomModel>? = null
    var pauseRefreshRepsone: LiveData<PauseRefreshRoomModel>? = null
    var rowCountResposne: LiveData<Int>? = null

    init {
        val currencyDB = CurrencyDB.getDatabase(application).CurrencyDao()
        repository = CurrencyRoomRepository(currencyDB)
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


    fun getRate(currecyCode: String): LiveData<CurrecnyRoomModel>? {
        currencyRateRepsone = repository.getRate(currecyCode)
        return currencyRateRepsone
    }

    fun getRowCount(): LiveData<Int>? {
        rowCountResposne = repository.getRowCount()
        return rowCountResposne
    }

    fun addPauseRefreshTime(pauseRefreshRoomModel: PauseRefreshRoomModel) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addRefressPauseTime(pauseRefreshRoomModel)
        }
    }

    fun deletePauseTime() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deletePauseTime()
        }
    }

    fun getPauseRefreshTime(): LiveData<PauseRefreshRoomModel>? {
        pauseRefreshRepsone = repository.getPauseRefreshTime()
        return pauseRefreshRepsone
    }





}