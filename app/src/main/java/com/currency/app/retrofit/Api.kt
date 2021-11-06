package com.currency.app.retrofit
import com.currency.app.model.CurrencyDataModel
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface Api {

    //http://apilayer.net/api/live?
    // access_key=0b3471b49fd30569f3fe054d1a6dd38b
    // &currencies=EUR,GBP,CAD,PLN&source=USD&format=1

    //@FormUrlEncoded
    @GET("live")
    fun getCurrencyData(
        @Query("access_key") access_key: String?,
       // @Query("currencies") currencies: String?,
        @Query("source") source: String?,
        @Query("format") format: String?,
    ): Single<CurrencyDataModel>

}