package com.ilham.etumarketbyseller.model.alltransaksi


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("_id")
    val id: String,
    @SerializedName("kode_transaksi")
    val kodeTransaksi: String,
    @SerializedName("Products")
    val products: List<Product>,
    @SerializedName("status")
    val status: String,
    @SerializedName("total")
    val total: Int,
    @SerializedName("user")
    val user: String,
    @SerializedName("__v")
    val v: Int
)