package com.ilham.etumarketbyseller.model.product.listpesanan


import com.google.gson.annotations.SerializedName

data class GetPesananResponse(
    @SerializedName("data")
    val `data`: List<DataPesanan>,
    @SerializedName("message")
    val message: String
)