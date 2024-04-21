package com.ilham.etumarketbyseller.model.pendapatan


import com.google.gson.annotations.SerializedName

data class GetPendapatanToko(
    @SerializedName("data")
    val `data`: List<DataToko>,
    @SerializedName("message")
    val message: String
)