package com.ilham.etumarketbyseller.model.alltransaksi


import com.google.gson.annotations.SerializedName

data class GetAllTransaksiResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String
)