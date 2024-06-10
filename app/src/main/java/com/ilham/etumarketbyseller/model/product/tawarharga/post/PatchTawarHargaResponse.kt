package com.ilham.etumarketbyseller.model.product.tawarharga.post


import com.google.gson.annotations.SerializedName

data class PatchTawarHargaResponse(
    @SerializedName("data")
    val `data`: Data,
    @SerializedName("message")
    val message: String
)