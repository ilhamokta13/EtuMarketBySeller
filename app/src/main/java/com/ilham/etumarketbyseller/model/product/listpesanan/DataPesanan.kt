package com.ilham.etumarketbyseller.model.product.listpesanan


import com.google.gson.annotations.SerializedName

data class DataPesanan(
    @SerializedName("_id")
    val id: String,
    @SerializedName("kode_transaksi")
    val kodeTransaksi: String,
    @SerializedName("Products")
    val products: List<Product>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("user")
    val user: User,
    @SerializedName("__v")
    val v: Int
)