package com.ilham.etumarketbyseller.model.product.delete


import com.google.gson.annotations.SerializedName

data class DeleteProductResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String
)