package com.currency.app.model

import com.google.gson.annotations.SerializedName

data class CurrencyDataModel(
    @SerializedName("success")
    var success: Boolean?,

    @SerializedName("timestamp")
    var timestamp: Int?,

   @SerializedName("quotes")
     var quotes: Map<String, Double>?



)

//data class Quotes(
//    @SerializedName("success")
//    var success: Boolean?,
//
//    @SerializedName("timestamp")
//    var timestamp: Int?
//)

data class ErrorDataModel(
    var errorCode: String? = "",
    var errorMessage: String? = "",
    )