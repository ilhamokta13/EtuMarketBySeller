package com.ilham.etumarketbyseller.model.product.tawarharga


import com.google.gson.annotations.SerializedName

data class GetResponseTawaranHarga(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("message")
    val message: String
)