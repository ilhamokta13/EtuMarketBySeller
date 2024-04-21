package com.ilham.etumarketbyseller.model.pendapatan


import com.google.gson.annotations.SerializedName

data class Products(
    @SerializedName("_id")
    val id: String,
    @SerializedName("ProductID")
    val productID: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("status")
    val status: String
)