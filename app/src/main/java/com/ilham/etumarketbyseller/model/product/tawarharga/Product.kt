package com.ilham.etumarketbyseller.model.product.tawarharga


import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("description")
    val description: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("price")
    val price: Int
)