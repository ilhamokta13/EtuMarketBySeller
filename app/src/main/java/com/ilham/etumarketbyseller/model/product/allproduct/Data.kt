package com.ilham.etumarketbyseller.model.product.allproduct


import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Data(
    @SerializedName("category")
    val category: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("nameProduct")
    val nameProduct: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("releaseDate")
    val releaseDate: String,
    @SerializedName("sellerID")
    val sellerID: SellerID,
    @SerializedName("__v")
    val v: Int
):Serializable