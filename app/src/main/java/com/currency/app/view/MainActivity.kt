package com.currency.app.view

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.currency.app.adapter.CurrecnyGridAdapter
import com.currency.app.room.CurrecnyRoomModel
import com.currency.app.room.PauseRefreshRoomModel
import com.currency.app.util.Constrants
import com.currency.app.viewmodel.CurrecnyRoomViewMoel


class MainActivity : AppCompatActivity() {

    private lateinit var currencyViewModel: CurrencyViewModel

    private lateinit var etAmount: EditText
    private lateinit var spCurrecncy: AppCompatSpinner
    private lateinit var tvConvertionAmont: TextView
    private lateinit var gvCurrency: GridView

    private lateinit var currencyCodes: Array<String>
    private lateinit var pDialog: ProgressDialog
    private lateinit var currencyList: java.util.ArrayList<CurrecnyRoomModel>
    private lateinit var currecnyRoomViewMoel: CurrecnyRoomViewMoel

    lateinit var mainHandler: Handler

    var from = ""
    var to = ""
    var conversionCurrencyCode = ""

    var isRunning: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currencyViewModel = ViewModelProvider(this).get(CurrencyViewModel::class.java)
        currecnyRoomViewMoel = ViewModelProvider(this).get(CurrecnyRoomViewMoel::class.java)
        mainHandler = Handler(Looper.getMainLooper())

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


        gvCurrency.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            val tvCurrencyCode =
                view.findViewById<View>(R.id.tvCurrencyCode) as TextView
            if (etAmount.text.toString().trim() == "") {
                Toast.makeText(applicationContext, "Please Enter Amount", Toast.LENGTH_SHORT).show()
            } else if (spCurrecncy.selectedItem.toString() == tvCurrencyCode.text.toString()) {
                Toast.makeText(
                    applicationContext,
                    "Currey Amount is Same to Currency Conversion.Please Select Different Conversion.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                conversionCurrencyCode = tvCurrencyCode.text.toString()
                var currencyCode =
                    spCurrecncy.selectedItem.toString() + conversionCurrencyCode
                currecnyRoomViewMoel.getRate(currencyCode)

                observeViewModel()
            }

        })




        if (!CheckNetwork.isOnline(this@MainActivity)) {
            Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
        } else {
            getRowCount();
        }


        currecySpinnerLoad()
        populateGridView()
        observeViewModel()
    }

    private fun observeViewModel() {

        currecnyRoomViewMoel.rowCountResposne?.observe(this, {
            it?.let {
                Log.e("row", it.toString())
                if (it == 0) {
                    getCurrencyData()
                }
            }
        })

        currencyViewModel.currecnyResponse.observe(this, {
            it?.let {
                pDialog.dismiss()
                currencyList = java.util.ArrayList<CurrecnyRoomModel>()
                currencyList.clear()
                if (it.success == true) {
                    currecnyRoomViewMoel.deleteAllCurrency()
                    val keys: ArrayList<String> = ArrayList(it.quotes!!.keys)
                    for (i in keys.indices) {
                        val currecyCode = keys[i]
                        var currencyRate = it.quotes?.get(currecyCode)

                        if (currecyCode.length > 3) {
                            from = currecyCode.substring(0, 3)
                            to = currecyCode.substring(currecyCode.length - 3, currecyCode.length)
                        }

                        var currecnyRoomModel =
                            currencyRate?.let { it1 ->
                                CurrecnyRoomModel(
                                    0, currecyCode, from, to, 1.0, it1
                                )
                            }
                        currecnyRoomModel?.let { it1 -> currecnyRoomViewMoel.addCurrency(it1) }
                    }

                } else {
                    Toast.makeText(applicationContext, "Currency Not Found.", Toast.LENGTH_SHORT)
                        .show()
                }

            }
        })

        currencyViewModel.errorResponse.observe(this, {
            it?.let {
                pDialog.dismiss()
                Toast.makeText(applicationContext, it.errorMessage, Toast.LENGTH_SHORT).show()

            }
        })

        currecnyRoomViewMoel.currencyRateRepsone?.observe(this, {
            it?.let {
                convertionAmount(it.currecyRate)
            }
        })



    }

    fun convertionAmount(currecyRate: Double) {

        try {
            var amount = etAmount.text.toString().trim().toDouble()
            var conversionAmount = amount * currecyRate

            tvConvertionAmont.setText(conversionAmount.toString() + " " + conversionCurrencyCode)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun currecySpinnerLoad() {

        val adapter: ArrayAdapter<String> =

            ArrayAdapter<String>(
                this,
                R.layout.spinner_text, currencyCodes
            )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCurrecncy.adapter = adapter


    }

    private fun populateGridView() {
        val adapter = CurrecnyGridAdapter(this@MainActivity, currencyCodes)
        gvCurrency.adapter = adapter
    }

    private fun getRowCount() {
        currecnyRoomViewMoel.getRowCount()
    }


    private fun getCurrencyData() {
        Log.e("***api***", "caling*****")
        // pDialog.show()

        val model = CurrencyModel()
        model.access_key = Constrants.access_key
        //model.currencies = "EUR,GBP,CAD,PLN"
        model.source = Constrants.source
        model.format = Constrants.format
        // this.let { currencyViewModel.getCurrencyData(model) }

    }

    //Api call after 30 minutes
    override fun onPause() {
        super.onPause()
        mainHandler.removeCallbacks(updateTextTask)
        currecnyRoomViewMoel.deletePauseTime()
        var pasusMillisecon = System.currentTimeMillis();
        var pauseTime = PauseRefreshRoomModel(0, pasusMillisecon, true)
        currecnyRoomViewMoel.addPauseRefreshTime(pauseTime)

    }

    override fun onResume() {
        super.onResume()
        checkPauseTime()
        mainHandler.post(updateTextTask)

    }

    private val updateTextTask = object : Runnable {
        override fun run() {
            mainHandler.postDelayed(this, Constrants.refreshApiTime)
            if (isRunning) {
                getCurrencyData()
            }
            isRunning = true

        }
    }

    private fun checkPauseTime() {
        currecnyRoomViewMoel.getPauseRefreshTime()
        pauseObservable()

    }

    fun pauseObservable() {

        currecnyRoomViewMoel.pauseRefreshRepsone?.observe(this, {
            it?.let {
                Log.e("pause ****",it.isPause.toString())
                currecnyRoomViewMoel.deletePauseTime()
                if (it.isPause) {
                    var difm = System.currentTimeMillis() - it.pasuseMillisecon
                    if (difm > Constrants.refreshApiTime) {

                        //refresh api
                        getCurrencyData()
                    }
                }
            }
        })
    }

}