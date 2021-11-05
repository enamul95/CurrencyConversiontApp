package com.currency.app

import android.app.Activity
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.currency.app.model.CurrencyModel
import com.currency.app.util.CheckNetwork
import com.currency.app.viewmodel.CurrencyViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var currencyViewModel: CurrencyViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currencyViewModel = ViewModelProvider(this).get(CurrencyViewModel::class.java)


        if (!CheckNetwork.isOnline(this@MainActivity)) {
            Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
        } else {

            getCurrencyData()
        }

        observeViewModel()
    }

    fun observeViewModel() {
        currencyViewModel.currecnyResponse.observe(this, androidx.lifecycle.Observer {
            it?.let {
              if(it.success == true){
                //  Log.e("quotes---->",",it.)
                //  Log.e("**quotes***********",it.quotes.toString())
                //  print("***********print*************")
                  //print(it.quotes)
              }else{
                  Toast.makeText(applicationContext, "Currency Not Found.", Toast.LENGTH_SHORT).show()
              }
               // Toast.makeText(applicationContext, "Succss"+it.quotes, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun getCurrencyData() {
      // pDialog.show()
        var model = CurrencyModel()
        model.access_key = "0b3471b49fd30569f3fe054d1a6dd38b"
        model.currencies = "EUR,GBP,CAD,PLN"
        model.source = "USD"
        model.format = "1"
        this?.let { it1 -> currencyViewModel.getCurrencyData(model) };
    }



}