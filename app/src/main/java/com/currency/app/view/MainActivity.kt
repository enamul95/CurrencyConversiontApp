package com.currency.app.view

import android.app.ProgressDialog
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
import com.currency.app.room.CurrecnyRoomModel
import com.currency.app.viewmodel.CurrecnyRoomViewMoel


class MainActivity : AppCompatActivity() {

    private lateinit var currencyViewModel: CurrencyViewModel

    private lateinit var etAmount: EditText
    private lateinit var spCurrecncy: AppCompatSpinner
    private lateinit var tvConvertionAmont: TextView
    private lateinit var gvCurrency: GridView

    private lateinit var currencyCodes: Array<String>
    private lateinit var pDialog:ProgressDialog
    private lateinit var currencyList: java.util.ArrayList<CurrecnyRoomModel>
    private lateinit var currecnyRoomViewMoel: CurrecnyRoomViewMoel

    var from = ""
    var to = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currencyViewModel = ViewModelProvider(this).get(CurrencyViewModel::class.java)
        currecnyRoomViewMoel = ViewModelProvider(this).get(CurrecnyRoomViewMoel::class.java)

        pDialog = ProgressDialog(this)
        pDialog.setTitle("Please wait...")

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
               // Toast.makeText(applicationContext, spCurrecncy.selectedItem.toString(), Toast.LENGTH_SHORT).show()

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

    private fun observeViewModel() {

        currencyViewModel.currecnyResponse.observe(this, {
            it?.let {
                pDialog.dismiss()
                currencyList = java.util.ArrayList<CurrecnyRoomModel>()
                currencyList.clear()
              if(it.success == true){
                  currecnyRoomViewMoel.deleteAllCurrency()
                  val keys: ArrayList<String> = ArrayList(it.quotes!!.keys)
                  for (i in keys.indices) {
                      val currecyCode = keys[i]
                     var currencyRate =  it.quotes?.get(currecyCode)

                      if (currecyCode.length > 3) {
                          from = currecyCode.substring(0,3)
                          to = currecyCode.substring(currecyCode.length - 3, currecyCode.length)
                      }

                      var currecnyRoomModel =
                          currencyRate?.let { it1 ->
                          CurrecnyRoomModel(
                              0,currecyCode, from,to, 1.0,it1
                          )
                      }
                      currecnyRoomModel?.let { it1 -> currecnyRoomViewMoel.addCurrency(it1) }
                     // currecnyRoomModel?.let { it1 -> currencyList.add(it1) }
                  }

              }else{
                  Toast.makeText(applicationContext, "Currency Not Found.", Toast.LENGTH_SHORT).show()
              }

            }
        })

        currencyViewModel.errorResponse.observe(this, {
            it?.let {
                pDialog.dismiss()
                Toast.makeText(applicationContext, it.errorMessage, Toast.LENGTH_SHORT).show()

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
      val adapter = CurrecnyGridAdapter(this@MainActivity, currencyCodes)
        gvCurrency.adapter = adapter
    }

    private fun getCurrencyData() {
       pDialog.show()

       // for (i in 0 until currencyCodes.size){
           // Log.e("currency--->",currencyCodes[i])
            val model = CurrencyModel()
            model.access_key = "0b3471b49fd30569f3fe054d1a6dd38b"
            //model.currencies = "EUR,GBP,CAD,PLN"
           model.source = "USD"
            model.format = "1"
            this.let { currencyViewModel.getCurrencyData(model) }
      //  }


    }



}