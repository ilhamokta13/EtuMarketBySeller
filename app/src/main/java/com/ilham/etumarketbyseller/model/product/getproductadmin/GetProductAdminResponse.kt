package com.ilham.etumarketbyseller.model.product.getproductadmin


import com.google.gson.annotations.SerializedName

data class GetProductAdminResponse(
    @SerializedName("data")
    val `data`: List<DataAdmin>,
    @SerializedName("message")
    val message: String
)