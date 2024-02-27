package com.ilham.etumarketbyseller.viewmodel

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ilham.etumarketbyseller.model.product.create.CreateProductResponse
import com.ilham.etumarketbyseller.model.product.create.Data
import com.ilham.etumarketbyseller.model.product.create.ResponseProductCreate
import com.ilham.etumarketbyseller.model.product.update.UpdateProductResponse
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
import kotlin.collections.ArrayList

@HiltViewModel
class ProductViewModel @Inject constructor(private val api : ApiService,
                                           private val sharedPreferences: SharedPreferences)
                                            : ViewModel() {
    private val _responsecreate : MutableLiveData<CreateProductResponse> = MutableLiveData()
    val responsecreate : LiveData<CreateProductResponse> = _responsecreate



    fun createproduct( nameProduct : RequestBody, price : RequestBody, description : RequestBody, image : MultipartBody.Part, category : RequestBody, token : String, releaseDate : RequestBody, latitude:RequestBody, longitude:RequestBody) {
        api.createproduct(nameProduct, price, description, image, category, "Bearer $token", releaseDate, latitude, longitude).enqueue(object : Callback<CreateProductResponse>{
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

    fun updateproduct( token: String, id: String, nameProduct : RequestBody, price : RequestBody, description : RequestBody, image : MultipartBody.Part, category : RequestBody, releaseDate : RequestBody, longitude: RequestBody, latitude: RequestBody){
        api.updateProduct( "Bearer $token", id, nameProduct, price, description, image, category, releaseDate, longitude, latitude).enqueue(object : Callback<com.ilham.etumarketbyseller.model.product.update.Data>{
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

    private val _responseupdate2 : MutableLiveData<com.ilham.etumarketbyseller.model.product.update.Data?> = MutableLiveData()
    val responseupdate2 : LiveData<com.ilham.etumarketbyseller.model.product.update.Data?> = _responseupdate


    fun updateproduct2(token : String, id : String,  nameProduct : String, price : String, description : String,  category : String, releaseDate : String, location:String){
        api.updateProduct2("Bearer $token", id, nameProduct, price, description, category, releaseDate, location).enqueue(object : Callback<com.ilham.etumarketbyseller.model.product.update.Data>{
            override fun onResponse(
                call: Call<com.ilham.etumarketbyseller.model.product.update.Data>,
                response: Response<com.ilham.etumarketbyseller.model.product.update.Data>
            ) {
                if (response.isSuccessful){
                    _responseupdate2.postValue(response.body())
                }else{
                    _responseupdate2.postValue(null)
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

    fun saveIdCart(cart: String){
        val editor = sharedPreferences.edit()
        editor.putString("idcart", cart)
        editor.apply()
    }

    fun getIdCart():String?{
        return sharedPreferences.getString("idcart"," ")
    }



//    fun saveIdProduct(idProduct:String){
//        val editor =  sharedPreferences.edit()
//        editor.putString("id", idProduct)
//        editor.apply()
//    }
//
//    fun getTicketId():String?{
//        return sharedPreferences.getString("id","")
//    }

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




    fun getLocation():String?{
        return sharedPreferences.getString("location"," ")
    }







}