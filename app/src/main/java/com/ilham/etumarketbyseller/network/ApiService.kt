package com.ilham.etumarketbyseller.network

import com.ilham.etumarketbyseller.model.ResponseRegister
import com.ilham.etumarketbyseller.model.login.LoginBody
import com.ilham.etumarketbyseller.model.login.ResponseLogin
import com.ilham.etumarketbyseller.model.product.allproduct.AllProductResponse
import com.ilham.etumarketbyseller.model.product.create.CreateProductResponse
import com.ilham.etumarketbyseller.model.product.delete.DeleteProductResponse
import com.ilham.etumarketbyseller.model.product.getproductadmin.GetProductAdminResponse
import com.ilham.etumarketbyseller.model.product.productperid.GetProductPerId
import com.ilham.etumarketbyseller.model.product.update.Data
import com.ilham.etumarketbyseller.model.product.update.UpdateProductResponse
import com.ilham.etumarketbyseller.model.profile.UpdateProfileResponse
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
    @POST("product")
    fun createproduct(
        @Part("nameProduct") nameProduct : RequestBody,
        @Part("price") price : RequestBody,
        @Part("description") description : RequestBody,
        @Part image : MultipartBody.Part,
        @Part ("category") category : RequestBody,
        @Header("Authorization") token: String,
        @Part("releaseDate") releaseDate : RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude:RequestBody

    ):Call<CreateProductResponse>

    @GET("product")
    fun getAllProduct(): Call<AllProductResponse>


    @DELETE("product/{id}")
    fun deleteProduct(
        @Header("Authorization") token: String,
        @Path("id") id:String
    ):Call<DeleteProductResponse>

    @Multipart
    @PATCH("product/{id}")
    fun updateProduct(
        @Header("Authorization") token: String,
        @Path("id") id:String,
        @Part("nameProduct") nameProduct : RequestBody,
        @Part("price") price : RequestBody,
        @Part("description") description : RequestBody,
        @Part image : MultipartBody.Part,
        @Part ("category") category : RequestBody,
        @Part("releaseDate") releaseDate : RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("latitude") latitude: RequestBody
    ):Call<com.ilham.etumarketbyseller.model.product.update.Data>

    @FormUrlEncoded
    @PATCH("product/{id}")
    fun updateProduct2(
        @Header("Authorization") token: String,
        @Path("id") id:String,
        @Part("nameProduct") nameProduct : String,
        @Part("price") price : String,
        @Field("description") description : String,
        @Field ("category") category : String,
        @Field("releaseDate") releaseDate : String,
        @Field("location") location: String

    ):Call<Data>



    @PATCH("product/update/{id}")
    fun updateseller(
        @Path("id") id:String,
        @Body request: com.ilham.etumarketbyseller.model.product.update.Data
    ):Call<UpdateProductResponse>


    @GET("product/{id}")

    fun getProductId(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ):Call<GetProductPerId>

    @GET("admin/product")
    fun getproductadmin(
        @Header("Authorization") token: String
    ):Call<GetProductAdminResponse>


    @FormUrlEncoded
    @PATCH("admin/complete-profile")
    fun updateprofile (
        @Header("Authorization") token: String,
        @Field("fullName") fullName : String,
        @Field("email") email : String,
        @Field("telp") telp : String,
        @Field("role") role:String,
        @Field("shopName") shopName:String
    ):Call<UpdateProfileResponse>




}