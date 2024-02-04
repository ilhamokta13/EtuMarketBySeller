package com.ilham.etumarketbyseller.model.product.create

import com.google.gson.annotations.SerializedName

data class ResponseProductCreate (
    @field:SerializedName("data")
    val `data`: DataCreate,
    @field:SerializedName("message")
    val message: String
    )

data class DataCreate(
    @field:SerializedName("category")
    val category: String,
    @field:SerializedName("description")
    val description: String,
    @field:SerializedName("_id")
    val id: String,
    @field:SerializedName("image")
    val image: String,
    @field:SerializedName("location")
    val location: String,
    @field:SerializedName("nameProduct")
    val nameProduct: String,
    @field:SerializedName("price")
    val price: Int,
    @field:SerializedName("releaseDate")
    val releaseDate: String,
    @field:SerializedName("sellerID")
    val sellerID: String,
    @field:SerializedName("__v")
    val v: Int
)
