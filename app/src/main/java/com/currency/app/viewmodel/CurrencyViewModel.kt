package com.currency.app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.currency.app.model.CurrencyDataModel
import com.currency.app.model.CurrencyModel
import com.currency.app.model.ErrorDataModel
import com.currency.app.retrofit.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class CurrencyViewModel : ViewModel() {

    private val apiService = ApiService()
    private val disposable = CompositeDisposable()

    var currecnyResponse = MutableLiveData<CurrencyDataModel>();


    var errorResponse = MutableLiveData<ErrorDataModel>();

    fun getCurrencyData(model: CurrencyModel) {

        disposable.add(apiService.getCurrencyData(model)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<CurrencyDataModel>() {
                override fun onSuccess(model: CurrencyDataModel) {
                    model?.let {
                        currecnyResponse.value = model
                    }

                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                   // errorResponse.value = RetrofitErrorMessage.getLoginErrorMessage(e)

                }

            })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

}