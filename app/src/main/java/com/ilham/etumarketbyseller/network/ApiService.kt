package com.ilham.etumarketbyseller.network

import com.ilham.etumarketbybuyer.model.transaksi.GetTransaksiResponse
import com.ilham.etumarketbybuyer.model.transaksi.PostTransaction
import com.ilham.etumarketbyseller.model.ResponseRegister
import com.ilham.etumarketbyseller.model.alltransaksi.GetAllTransaksiResponse
import com.ilham.etumarketbyseller.model.login.LoginBody
import com.ilham.etumarketbyseller.model.login.ResponseLogin
import com.ilham.etumarketbyseller.model.pendapatan.GetPendapatanToko
import com.ilham.etumarketbyseller.model.product.allproduct.AllProductResponse
import com.ilham.etumarketbyseller.model.product.create.CreateProductResponse
import com.ilham.etumarketbyseller.model.product.delete.DeleteProductResponse
import com.ilham.etumarketbyseller.model.product.getproductadmin.GetProductAdminResponse
import com.ilham.etumarketbyseller.model.product.listpesanan.GetPesananResponse
import com.ilham.etumarketbyseller.model.product.productperid.GetProductPerId
import com.ilham.etumarketbyseller.model.product.status.*
import com.ilham.etumarketbyseller.model.product.tawarharga.GetResponseTawaranHarga
import com.ilham.etumarketbyseller.model.product.tawarharga.post.PatchTawarHargaResponse
import com.ilham.etumarketbyseller.model.product.update.Data
import com.ilham.etumarketbyseller.model.product.update.UpdateProductResponse
import com.ilham.etumarketbyseller.model.profile.UpdateProfileResponse
import com.ilham.etumarketbyseller.model.profile.allprofile.AllProfileResponse
import com.ilham.etumarketbyseller.model.profile.profilebyid.ProfilebyIdResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("user/register")
    fun register(
        @Field("userId") userId : String,
        @Field("fullName") fullName : String,
        @Field("email") email : String,
        @Field("password") password : String,
        @Field("telp") telp : String,
        @Field("role") role:String,
        @Field("shopName") shopName : String
    ):Call<ResponseRegister>

    @POST("user/login")
    fun login(@Body loginBody: LoginBody) : Call<ResponseLogin>

    @Multipart
    @POST("product")
    fun createproduct(
        @Part("userId")  userId: String,
        @Part("nameProduct") nameProduct : RequestBody,
        @Part("price") price : RequestBody,
        @Part("description") description : RequestBody,
        @Part image : MultipartBody.Part,
        @Part ("category") category : RequestBody,
        @Header("Authorization") token: String,
        @Part("releaseDate") releaseDate : RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude:RequestBody,
        @Part("stock") stock :RequestBody

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
        @Part("latitude") latitude: RequestBody,
        @Part("stock") stock: RequestBody
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

    @GET("transaksi/get")
    fun getAllTransaksi(@Header("Authorization") token: String): Call<GetAllTransaksiResponse>


    @POST("transaksi/create")
    fun posttransaksi(
        @Header("Authorization") token: String,
        @Body request: PostTransaction
    ):Call<GetTransaksiResponse>

    @GET("transaksi/getAdminTransaksi")
    fun gethistory(
        @Header("Authorization") token: String
    ):Call<GetPesananResponse>

    @FormUrlEncoded
    @PATCH("/transaksi/updateStatus")
    fun poststatus(
        @Header("Authorization") token: String,
        @Field("kode_transaksi") kode_transaksi : String,
        @Field("productID") productID: String,
        @Field("status") status: String,
    ):Call<ResponseUpdateStatus>

    @Multipart
    @PATCH("/transaksi/updateStatus")
    fun poststatusimage(
        @Header("Authorization") token: String,
        @Part image : MultipartBody.Part,
    ):Call<ResponseUpdateStatus>


    @POST("user/new-password")
    fun newpass(
        @Body postNewPassword: PostNewPassword
    ) : Call<ResponseNewPass>


    @POST("user/forgot-password")
    fun forgotpass(
        @Body postForgotPass: PostForgotPass) : Call<ResponseForgotPass>



        @GET("/transaksi/getPendapatan")
        fun getpendapatan(
            @Header("Authorization") token: String,
        ):Call<GetPendapatanToko>

        @GET("user/allprofiles")
        fun getallprofile(
            @Header("Authorization") token: String,

        ):Call<AllProfileResponse>

    @GET("/user/profile")
    fun getprofile(
        @Header("Authorization") token: String
    ):Call<ProfilebyIdResponse>

    @FormUrlEncoded
    @PATCH("admin/complete-profile")
    fun updateprofileemail(
        @Header("Authorization") token: String,
        @Field("email") email : String,
    ):Call<UpdateProfileResponse>

    @GET("product/{id}/offers/status")
    fun gettawar(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ):Call<GetResponseTawaranHarga>

    @FormUrlEncoded
    @PATCH("product/{id}/offer/{offerId}")
    fun postresponstawar(
        @Header("Authorization") token: String,
        @Path("id") id: String,
        @Path("offerId") offerId:String,
        @Field("status") status: String
    ):Call<PatchTawarHargaResponse>











}