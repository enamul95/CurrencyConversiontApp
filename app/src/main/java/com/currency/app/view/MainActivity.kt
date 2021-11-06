package com.currency.app.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.lifecycle.ViewModelProvider
import com.currency.app.R
import com.currency.app.model.CurrencyModel
import com.currency.app.util.CheckNetwork
import com.currency.app.viewmodel.CurrencyViewModel
import android.widget.ArrayAdapter
import com.currency.app.adapter.CurrecnyGridAdapter


class MainActivity : AppCompatActivity() {

    private lateinit var currencyViewModel: CurrencyViewModel

    private lateinit var etAmount: EditText
    private lateinit var spCurrecncy: AppCompatSpinner
    private lateinit var tvConvertionAmont: TextView
    private lateinit var gvCurrency: GridView

    private lateinit var currencyCodes: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currencyViewModel = ViewModelProvider(this).get(CurrencyViewModel::class.java)

        etAmount = findViewById(R.id.etAmount)
        spCurrecncy = findViewById(R.id.spCurrecncy)
        tvConvertionAmont = findViewById(R.id.tvConvertionAmont)

        gvCurrency = findViewById(R.id.gvCurrency)

        currencyCodes = resources.getStringArray(R.array.currency_codes)

        spCurrecncy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                Toast.makeText(applicationContext, spCurrecncy.selectedItem.toString(), Toast.LENGTH_SHORT).show()

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        if (!CheckNetwork.isOnline(this@MainActivity)) {
            Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
        } else {

            getCurrencyData()
        }

        currecySpinnerLoad()
        populateGridView()
        observeViewModel()
    }

    fun observeViewModel() {
        currencyViewModel.currecnyResponse.observe(this, {
            it?.let {
              if(it.success == true){
                //  Log.e("quotes---->",",it.)
                 Log.e("**quotes***********",it.quotes.toString())
                //  print("***********print*************")
                  //print(it.quotes)
              }else{
                  Toast.makeText(applicationContext, "Currency Not Found.", Toast.LENGTH_SHORT).show()
              }
               // Toast.makeText(applicationContext, "Succss"+it.quotes, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun currecySpinnerLoad() {

        val adapter: ArrayAdapter<String> =

         ArrayAdapter<String>(
                    this,
                    R.layout.spinner_text, currencyCodes)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCurrecncy.adapter = adapter


    }

    private fun populateGridView(){
//        val adapter: ArrayAdapter<String> =
//            ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, currencyCodes)
//        gvCurrency.adapter = adapter

      var  adapter = CurrecnyGridAdapter(this@MainActivity, currencyCodes)
        gvCurrency.adapter = adapter
    }

    private fun getCurrencyData() {
      // pDialog.show()
        val model = CurrencyModel()
        model.access_key = "0b3471b49fd30569f3fe054d1a6dd38b"
        model.currencies = "EUR,GBP,CAD,PLN"
        model.source = "USD"
        model.format = "1"
        this.let { currencyViewModel.getCurrencyData(model) }
    }



}