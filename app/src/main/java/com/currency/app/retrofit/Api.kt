package com.currency.app.retrofit

import com.currency.app.model.CurrencyDataModel
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface Api {

    @GET("live")
    fun getCurrencyData(
        @Query("access_key") access_key: String?,
        // @Query("currencies") currencies: String?,
        @Query("source") source: String?,
        @Query("format") format: String?,
    ): Single<CurrencyDataModel>

}