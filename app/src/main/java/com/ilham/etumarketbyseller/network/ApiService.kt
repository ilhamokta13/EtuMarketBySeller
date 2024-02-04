package com.ilham.etumarketbyseller.network

import android.location.Location
import com.ilham.etumarketbyseller.model.ResponseRegister
import com.ilham.etumarketbyseller.model.login.LoginBody
import com.ilham.etumarketbyseller.model.login.ResponseLogin
import com.ilham.etumarketbyseller.model.product.allproduct.SellerID
import com.ilham.etumarketbyseller.model.product.create.Data
import com.ilham.etumarketbyseller.model.product.create.ResponseProductCreate
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("user/register")
    fun register(
        @Field("fullName") fullName : String,
        @Field("email") email : String,
        @Field("password") password : String,
        @Field("telp") telp : String,
        @Field("role") role:String,
    ):Call<ResponseRegister>

    @POST("user/login")
    fun login(@Body loginBody: LoginBody) : Call<ResponseLogin>

    @Multipart
    @POST("product/create")
    fun createproduct(
        @Part("nameProduct") nameProduct : RequestBody,
        @Part("price") price : RequestBody,
        @Part("description") description : RequestBody,
        @Part image : MultipartBody.Part,
        @Part ("category") category : RequestBody,
        @Header("Authorization") token: String,
        @Part("releaseDate") releaseDate : RequestBody,
        @Part("location") location: RequestBody

    ):Call<Data>

    @GET("product/all")
    fun getAllProduct(): Call<List<com.ilham.etumarketbyseller.model.product.allproduct.Data>>


    @DELETE("product/delete/{id}")
    fun deleteProduct(
        @Path("id") id:String
    ):Call<com.ilham.etumarketbyseller.model.product.delete.Data>

    @Multipart
    @POST("product/update/{id}")
    fun updateProduct(
        @Part("nameProduct") nameProduct : RequestBody,
        @Part("price") price : RequestBody,
        @Part("description") description : RequestBody,
        @Part image : MultipartBody.Part,
        @Part ("category") category : RequestBody,
        @Header("Authorization") token: String,
        @Part("releaseDate") releaseDate : RequestBody,
        @Part("location") location: RequestBody

    ):Call<com.ilham.etumarketbyseller.model.product.update.Data>




}