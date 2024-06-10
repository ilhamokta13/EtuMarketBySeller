package com.ilham.etumarketbyseller.model.product.tawarharga.post


import com.google.gson.annotations.SerializedName

data class Offer(
    @SerializedName("buyerID")
    val buyerID: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("status")
    val status: String
)