package com.currency.app.retrofit
import com.currency.app.model.CurrencyDataModel
import com.currency.app.model.CurrencyModel
import com.currency.app.util.IpConfigure
import io.reactivex.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class ApiService {

    var baseurl = IpConfigure.getIp()

    var okHttpClient: OkHttpClient? = OkHttpClient.Builder()
        .connectTimeout(40, TimeUnit.SECONDS)
        .readTimeout(40, TimeUnit.SECONDS)
        .writeTimeout(40, TimeUnit.SECONDS)
        .build()

    private val api = Retrofit.Builder()
        .baseUrl(baseurl)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(okHttpClient)
        .build()
        .create(Api::class.java);

    fun getCurrencyData(model: CurrencyModel): Single<CurrencyDataModel> {
        return api.getCurrencyData(
            model.access_key,
            model.currencies,
            model.source,
            model.format

        )
    }

}