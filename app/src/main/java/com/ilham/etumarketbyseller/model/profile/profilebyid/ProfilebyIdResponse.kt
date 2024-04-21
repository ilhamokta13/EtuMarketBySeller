package com.ilham.etumarketbyseller.model.profile.profilebyid


import com.google.gson.annotations.SerializedName

data class ProfilebyIdResponse(
    @SerializedName("data")
    val `data`: UserProfile,
    @SerializedName("error")
    val error: Boolean,
    @SerializedName("message")
    val message: String
)