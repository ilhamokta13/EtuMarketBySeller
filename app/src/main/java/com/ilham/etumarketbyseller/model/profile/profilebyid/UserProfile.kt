package com.ilham.etumarketbyseller.model.profile.profilebyid


import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("email")
    val email: String,
    @SerializedName("fullName")
    val fullName: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("role")
    val role: String,
    @SerializedName("shopName")
    val shopName: String,
    @SerializedName("telp")
    val telp: String,
    @SerializedName("__v")
    val v: Int
)