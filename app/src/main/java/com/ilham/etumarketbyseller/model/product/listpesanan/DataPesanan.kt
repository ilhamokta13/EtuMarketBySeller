package com.ilham.etumarketbyseller.model.product.listpesanan


import com.google.gson.annotations.SerializedName

data class DataPesanan(
    @SerializedName("destination")
    val destination: Destination,
    @SerializedName("kode_transaksi")
    val kodeTransaksi: String,
    @SerializedName("products")
    val products: List<Product>,
    @SerializedName("shippingCost")
    val shippingCost: Int,
    @SerializedName("status")
    val status: String,
    @SerializedName("total")
    val total: Int,
    @SerializedName("transaksiId")
    val transaksiId: String,
    @SerializedName("user")
    val user: User
)