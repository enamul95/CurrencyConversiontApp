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


    private lateinit var etAmount: EditText
    private lateinit var spCurrecncy: AppCompatSpinner
    private lateinit var tvConvertionAmont: TextView
    private lateinit var gvCurrency: GridView

    private lateinit var currencyCodes: Array<String>
    private lateinit var pDialog: ProgressDialog

    private lateinit var currencyList: java.util.ArrayList<CurrecnyRoomModel>
    private lateinit var currencyViewModel: CurrencyViewModel
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
                currecnyRoomViewMoel.getSourceCurrencyRate(currencyCode)
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
            currecnyRoomViewMoel.getRowCount()
        }

        currecnyRoomViewMoel.getCurrencyList()
        currecnyRoomViewMoel.getPauseRefreshTime();

        currecySpinnerLoad()
        populateGridView()
        observeViewModel()
    }

    private fun observeViewModel() {

        //check currency is empty. if empty then load from currency layer api
        currecnyRoomViewMoel.rowCountResposne?.observe(this, {
            it?.let {
                if (it == 0) {
                    getCurrencyData()
                }
            }
        })

        //get all Currecy from SQLITE DB
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
                //reload data
                populateGridView()
                currecySpinnerLoad()

            }
        })
        //get Currencies data from API & store data in sqlite db
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

        //Error Response from api
        currencyViewModel.errorResponse.observe(this, {
            it?.let {
                pDialog.dismiss()
                Toast.makeText(applicationContext, it.errorMessage, Toast.LENGTH_SHORT).show()

            }
        })

        //Screen off or on count 30 minutes time
        currecnyRoomViewMoel.pauseRefreshRepsone?.observe(this, {
            it?.let {
                currecnyRoomViewMoel.deletePauseTime()
                var difm = System.currentTimeMillis() - it.pasuseMillisecon
                if (difm > Constrants.refreshApiTime) {
                    getCurrencyData()
                }
            }
        })
    }

    //source Currency observable
    fun sourceCurrencyObserable() {
        currecnyRoomViewMoel.sourceCurrencyRateRepsone?.observe(this, {
            it?.let {
                sourceCurrencyRate = it.currecyRate
            }
        })

    }

    //Convertion Currency observable
    fun conversionCurrencyObserable() {

        currecnyRoomViewMoel.conversionCurrencyRateRepsone?.observe(this, {
            it?.let {
                conversionCurrencyRate = it.currecyRate
                convertionAmount()
            }
        })

    }

    //convert source currency to converstion currency
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

    //populate currency spinner
    private fun currecySpinnerLoad() {
        var operatorAdapter = CurrencySpinnerAdapter(this, currencyAdapterList)
        spCurrecncy.adapter = operatorAdapter
    }

    //populate currency gride
    private fun populateGridView() {
        var adapter = CurrecnyGridAdapter(this, currencyAdapterList)
        gvCurrency.adapter = adapter
    }

    //api calling
    private fun getCurrencyData() {
        val model = CurrencyModel()
        model.access_key = Constrants.access_key
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
    }

    // refresg api after 30 minutes
    private val updateTextTask = object : Runnable {
        override fun run() {
            mainHandler.postDelayed(this, Constrants.refreshApiTime)
            if (isRunning) {
                getCurrencyData()
            }
            isRunning = true

        }
    }


}