package com.ilham.etumarketbyseller.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ilham.etumarketbyseller.model.product.create.CreateProductResponse
import com.ilham.etumarketbyseller.model.product.create.Data
import com.ilham.etumarketbyseller.model.product.tawarharga.GetResponseTawaranHarga
import com.ilham.etumarketbyseller.model.product.tawarharga.TawarProduct
import com.ilham.etumarketbyseller.model.product.tawarharga.post.PatchTawarHargaResponse
import com.ilham.etumarketbyseller.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(private val api : ApiService,
                                           private val sharedPreferences: SharedPreferences)
                                            : ViewModel() {
    private val _responsecreate : MutableLiveData<CreateProductResponse> = MutableLiveData()
    val responsecreate : LiveData<CreateProductResponse> = _responsecreate



    fun createproduct( userId: String,nameProduct : RequestBody, price : RequestBody, description : RequestBody, image : MultipartBody.Part, category : RequestBody, token : String, releaseDate : RequestBody, latitude:RequestBody, longitude:RequestBody, stock : RequestBody) {
        api.createproduct(userId,nameProduct, price, description, image, category, "Bearer $token", releaseDate, latitude, longitude, stock).enqueue(object : Callback<CreateProductResponse>{
            override fun onResponse(
                call: Call<CreateProductResponse>,
                response: Response<CreateProductResponse>
            ) {
                if (response.isSuccessful){
                    _responsecreate.postValue(response.body())
                }else{
                    Log.e("ProductViewModel","${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<CreateProductResponse>, t: Throwable) {
                Log.e("ProductViewModel", "Cannot create data")
            }

        })

    }

    private val _responseupdate : MutableLiveData<com.ilham.etumarketbyseller.model.product.update.Data?> = MutableLiveData()
    val responseupdate : LiveData<com.ilham.etumarketbyseller.model.product.update.Data?> = _responseupdate

    fun updateproduct( token: String, id: String, nameProduct : RequestBody, price : RequestBody, description : RequestBody, image : MultipartBody.Part, category : RequestBody, releaseDate : RequestBody, longitude: RequestBody, latitude: RequestBody, stock: RequestBody){
        api.updateProduct( "Bearer $token", id, nameProduct, price, description, image, category, releaseDate, longitude, latitude, stock).enqueue(object : Callback<com.ilham.etumarketbyseller.model.product.update.Data>{
            override fun onResponse(
                call: Call<com.ilham.etumarketbyseller.model.product.update.Data>,
                response: Response<com.ilham.etumarketbyseller.model.product.update.Data>
            ) {
                if (response.isSuccessful){
                    _responseupdate.postValue(response.body())
                }else{
                    _responseupdate.postValue(null)
                    Log.e("ProductViewModel","${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(
                call: Call<com.ilham.etumarketbyseller.model.product.update.Data>,
                t: Throwable
            ) {
                Log.e("ProductViewModel", "Cannot get data")
            }

        })

    }




    private val liveDatatawarharga: MutableLiveData<List<TawarProduct>> =  MutableLiveData()
    val datatawarharga: LiveData<List<TawarProduct>> = liveDatatawarharga

    fun tawarharga(token : String, id : String){
        api.gettawar("Bearer $token", id).enqueue(object : Callback<GetResponseTawaranHarga>{
            override fun onResponse(
                call: Call<GetResponseTawaranHarga>,
                response: Response<GetResponseTawaranHarga>
            ) {

                if (response.isSuccessful) {
                    val responseData = response.body()
                    if (responseData != null && responseData.data!= null) {
                        val cartProducts = responseData.data
                        liveDatatawarharga.value = flattenCartData(responseData.data)
                    } else {
                        Log.e("CartUserViewModel", "Response body or products are null")
                    }
                } else {
                    Log.e("CartUserViewModel", "${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<GetResponseTawaranHarga>, t: Throwable) {
                Log.e("CartViewModel3", "Failed to fetch cart data: ${t.message}")

            }

        })
    }

    private val confirmstatusharga : MutableLiveData<PatchTawarHargaResponse> = MutableLiveData()
    val dataconfirmstatusharga : LiveData<PatchTawarHargaResponse> = confirmstatusharga

    fun confirmtawarharga(token: String, id: String, offerId : String, status:String){
        api.postresponstawar("Bearer $token", id, offerId, status).enqueue(object : Callback<PatchTawarHargaResponse>{
            override fun onResponse(
                call: Call<PatchTawarHargaResponse>,
                response: Response<PatchTawarHargaResponse>
            ) {
                if (response.isSuccessful) {
                    confirmstatusharga.value = response.body()
                } else {
                    Log.e("ConfirmStatusHarga", "${response.errorBody()?.string()}")

                }
            }
            override fun onFailure(call: Call<PatchTawarHargaResponse>, t: Throwable) {
                Log.e("ConfirmStatusHarga2", "Null Post Confirm Status")
            }

        })

    }


    fun flattenCartData(dataList: List<com.ilham.etumarketbyseller.model.product.tawarharga.Data>): List<TawarProduct> {
        val productList = mutableListOf<TawarProduct>()
        for (data in dataList) {
            val namaproduk = data.product.name
            val hargaproduk = data.product.price
            val idproduct = data.product.id
            for (product in data.offers) {
                productList.add(TawarProduct(namaproduk,hargaproduk,product, idproduct))
            }
        }
        return productList
    }



    fun saveIdCart(cart: String){
        val editor = sharedPreferences.edit()
        editor.putString("idcart", cart)
        editor.apply()
    }



    fun saveDate(date: String){
        val editor = sharedPreferences.edit()
        editor.putString("date",date)
        editor.apply()
    }

    fun saveEditDate(date: String){
        val editor = sharedPreferences.edit()
        editor.putString("edit",date)
        editor.apply()
    }

    fun saveLocation(location: String){
        val editor = sharedPreferences.edit()
        editor.putString("location", location)
        editor.apply()
    }

    fun getDate():String?{

        val defaultTanggal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val storedDate = sharedPreferences.getString("date", defaultTanggal)
        val timeZone = TimeZone.getTimeZone("UTC")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.timeZone = timeZone
        val utcDate = dateFormat.parse(storedDate)

        val calendar = Calendar.getInstance()
        calendar.time = utcDate
        calendar.add(Calendar.HOUR_OF_DAY, -calendar.timeZone.rawOffset / (60 * 60 * 1000))

        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    }

    fun geteditdate():String?{

        val defaultTanggal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val storedDate = sharedPreferences.getString("edit", defaultTanggal)

        val timeZone = TimeZone.getTimeZone("UTC")
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        dateFormat.timeZone = timeZone
        val utcDate = dateFormat.parse(storedDate)

        val calendar = Calendar.getInstance()
        calendar.time = utcDate
        calendar.add(Calendar.HOUR_OF_DAY, -calendar.timeZone.rawOffset / (60 * 60 * 1000))

        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
    }








}