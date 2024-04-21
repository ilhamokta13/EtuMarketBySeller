package com.ilham.etumarketbyseller.model.alltransaksi


import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("_id")
    val id: String,
    @SerializedName("ProductID")
    val productID: ProductID,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("status")
    val status: String
)