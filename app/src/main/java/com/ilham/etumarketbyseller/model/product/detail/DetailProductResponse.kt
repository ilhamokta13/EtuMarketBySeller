package com.ilham.etumarketbyseller.model.product.detail


import com.google.gson.annotations.SerializedName

data class DetailProductResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String
)