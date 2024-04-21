package com.ilham.etumarketbyseller.model.pendapatan


import com.google.gson.annotations.SerializedName

data class TransaksiToko(
    @SerializedName("_id")
    val id: String,
    @SerializedName("kode_transaksi")
    val kodeTransaksi: String,
    @SerializedName("Product")
    val product: Product,
    @SerializedName("Products")
    val products: Products,
    @SerializedName("status")
    val status: String,
    @SerializedName("total")
    val total: Int,
    @SerializedName("user")
    val user: String,
    @SerializedName("__v")
    val v: Int
)