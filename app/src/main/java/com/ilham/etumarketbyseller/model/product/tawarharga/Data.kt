package com.ilham.etumarketbyseller.model.product.tawarharga


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("offers")
    val offers: List<Offer>,
    @SerializedName("product")
    val product: Product
)