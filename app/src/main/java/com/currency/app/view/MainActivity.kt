package com.currency.app.view

import android.app.ProgressDialog
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
import com.currency.app.adapter.CurrecnyGridAdapter
import com.currency.app.adapter.CurrencySpinnerAdapter
import com.currency.app.model.CurrencyAdapterModel
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
    var conversionCurrency = ""
    var conversionCurrencyCode = ""
    var sourceCurrencyRate: Double = 0.0
    var conversionCurrencyRate: Double = 0.0
    var isRunning: Boolean = false
    private var currencyAdapterList: java.util.ArrayList<CurrencyAdapterModel> =
        java.util.ArrayList<CurrencyAdapterModel>()

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
                var tv_currecy_code: TextView? = view?.findViewById(R.id.tv_currecy_code)
                var currencyCode = tv_currecy_code?.text.toString()
                Toast.makeText(applicationContext, currencyCode, Toast.LENGTH_SHORT).show()
                // if(tv_currecy_code?.text.toString() != ""){
                currecnyRoomViewMoel.getSourceCurrencyRate(currencyCode)
                // }
                sourceCurrencyObserable()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        gvCurrency.setOnItemClickListener(AdapterView.OnItemClickListener { parent, view, position, id ->
            val tvCurrencyCode =
                view.findViewById<View>(R.id.tvCurrencyCode) as TextView
            if (etAmount.text.toString().trim() == "") {
                Toast.makeText(applicationContext, "Please Enter Amount", Toast.LENGTH_SHORT).show()
            } else {
                conversionCurrencyCode = tvCurrencyCode.text.toString()
                currecnyRoomViewMoel.getConversionCurrencyRate(conversionCurrencyCode)
                conversionCurrencyObserable()
            }

        })




        if (!CheckNetwork.isOnline(this@MainActivity)) {
            Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
        } else {
            getRowCount();
        }

        currecnyRoomViewMoel.getCurrencyList()

        currecySpinnerLoad()
        populateGridView()
        observeViewModel()
    }

    private fun observeViewModel() {

        currecnyRoomViewMoel.rowCountResposne?.observe(this, {
            it?.let {
                if (it == 0) {
                    getCurrencyData()
                }
            }
        })

        currecnyRoomViewMoel.currencyListRepsone?.observe(this, {
            it?.let {
                for (i in 0 until it!!.size) {
                    val model = CurrencyAdapterModel(
                        it[i].id,
                        it[i].currecyCode.toString(),
                        it[i].from.toString(),
                        it[i].conversionCurrency.toString(),
                        it[i].currecyRate
                    )
                    currencyAdapterList.add(model)
                }
                populateGridView()
                currecySpinnerLoad()

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
                            conversionCurrency =
                                currecyCode.substring(currecyCode.length - 3, currecyCode.length)
                        }

                        var currecnyRoomModel =
                            currencyRate?.let { it1 ->
                                CurrecnyRoomModel(
                                    0, currecyCode, from, conversionCurrency, currencyRate,
                                )
                            }
                        currecnyRoomModel?.let { it1 -> currecnyRoomViewMoel.addCurrency(it1) }
                    }

                    finish();
                    startActivity(getIntent());

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
    }

    fun sourceCurrencyObserable() {

        currecnyRoomViewMoel.sourceCurrencyRateRepsone?.observe(this, {
            it?.let {
                sourceCurrencyRate = it.currecyRate
            }
        })

    }

    fun conversionCurrencyObserable() {

        currecnyRoomViewMoel.conversionCurrencyRateRepsone?.observe(this, {
            it?.let {
                conversionCurrencyRate = it.currecyRate
                convertionAmount()
            }
        })

    }


    fun convertionAmount() {

        try {
            var amount = etAmount.text.toString().trim().toDouble()
            var conversionAmount = (conversionCurrencyRate / sourceCurrencyRate) * amount
            var convertTwoDigit = String.format("%.2f", conversionAmount)
            tvConvertionAmont.setText(convertTwoDigit + " " + conversionCurrencyCode)

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun currecySpinnerLoad() {
        var operatorAdapter = CurrencySpinnerAdapter(this, currencyAdapterList)
        spCurrecncy.adapter = operatorAdapter
    }

    private fun populateGridView() {
        var adapter = CurrecnyGridAdapter(this, currencyAdapterList)
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
        this.let { currencyViewModel.getCurrencyData(model) }

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

        mainHandler.post(updateTextTask)

        // checkPauseTime()

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