package com.ilham.etumarketbyseller.model.product.allproduct


import com.google.gson.annotations.SerializedName

data class AllProductResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String
)