package com.ilham.etumarketbyseller.model.product.update


import com.google.gson.annotations.SerializedName

data class UpdateProductResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String
)