package com.ilham.etumarketbyseller.model.profile.allprofile


import com.google.gson.annotations.SerializedName

data class AllProfileResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String
)