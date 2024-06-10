package com.ilham.etumarketbyseller.model.product.status

import com.google.gson.annotations.SerializedName

class PostNewPassword (
    @SerializedName("newPassword")
    val newPassword: String,
    @SerializedName("token")
    val token: String
    )