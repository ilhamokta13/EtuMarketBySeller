package com.ilham.etumarketbyseller.model.product.status

import com.google.gson.annotations.SerializedName

class ResponseNewPass (
        @SerializedName("error")
        val error: Boolean,
        @SerializedName("message")
        val message: String
        )