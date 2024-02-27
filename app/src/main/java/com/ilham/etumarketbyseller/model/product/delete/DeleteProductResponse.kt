package com.ilham.etumarketbyseller.model.product.delete


import com.google.gson.annotations.SerializedName
import com.ilham.etumarketbyseller.model.product.getproductadmin.DataAdmin

data class DeleteProductResponse(
    @SerializedName("data")
    val `data`: DataAdmin,
    @SerializedName("message")
    val message: String
)