package com.ilham.etumarketbyseller.model.product.productperid


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetProductPerId(
    @SerializedName("data")
    val `data`: DataPerId,
    @SerializedName("message")
    val message: String
):Serializable