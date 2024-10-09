package com.ilham.etumarketbyseller.model.product.getproductadmin


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable
@Parcelize
data class DataAdmin(
    @SerializedName("category")
    val category: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("_id")
    val id: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
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
):Parcelable