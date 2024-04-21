package com.ilham.etumarketbyseller.model.pendapatan


import com.google.gson.annotations.SerializedName

data class DataToko(
    @SerializedName("sellerID")
    val sellerID: String,
    @SerializedName("sellerName")
    val sellerName: String,
    @SerializedName("totalPendapatan")
    val totalPendapatan: Int,
    @SerializedName("transaksi")
    val transaksi: List<TransaksiToko>
)