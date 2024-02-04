package com.ilham.etumarketbyseller.model.product.create


import com.google.gson.annotations.SerializedName

data class CreateProductResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String
)