package com.currency.app.model

class CurrencyAdapterModel(
    id: Int,
    currecyCode: String,
    from: String,
    conversionCurrency: String,
    currecyRate: Double
) {
    var id: Int = 0
    var currecyCode: String? = ""
    var from: String? = ""
    var conversionCurrency: String? = ""
    var currencyCode: String? = ""
    var currecyRate: Double = 0.0


    init {
        this.id = id
        this.currecyCode = currecyCode
        this.from = from
        this.conversionCurrency = conversionCurrency
        this.currencyCode = currencyCode
        this.currecyRate = currecyRate

    }
}