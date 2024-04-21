package com.ilham.etumarketbyseller.model.product.status


import com.google.gson.annotations.SerializedName

data class PostUpdateStatus(
    @SerializedName("kode_transaksi")
    val kodeTransaksi: String,
    @SerializedName("productID")
    val productID: String,
    @SerializedName("status")
    val status: String
)