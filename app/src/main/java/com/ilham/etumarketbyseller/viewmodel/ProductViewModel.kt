package com.ilham.etumarketbyseller.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ilham.etumarketbyseller.model.product.create.CreateProductResponse
import com.ilham.etumarketbyseller.model.product.create.Data
import com.ilham.etumarketbyseller.model.product.create.ResponseProductCreate
import com.ilham.etumarketbyseller.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(private val api : ApiService) : ViewModel() {
    private val _responsecreate : MutableLiveData<Data?> = MutableLiveData()
    val responsecreate : LiveData<Data?> = _responsecreate



    fun createproduct(nameProduct : RequestBody, price : RequestBody, description : RequestBody, image : MultipartBody.Part, category : RequestBody, token : String, releaseDate : RequestBody, location:RequestBody) {
        api.createproduct(nameProduct, price, description, image, category, "Bearer $token", releaseDate, location).enqueue(object : Callback<Data>{
            override fun onResponse(call: Call<Data>, response: Response<Data>) {
                if (response.isSuccessful){
                    _responsecreate.postValue(response.body())
                }else{
                    _responsecreate.postValue(null)
                    error(response.message())
                }
            }

            override fun onFailure(call: Call<Data>, t: Throwable) {
                error(t)
            }

        })

    }

    private val _responseupdate : MutableLiveData<com.ilham.etumarketbyseller.model.product.update.Data?> = MutableLiveData()
    val responseupdate : LiveData<com.ilham.etumarketbyseller.model.product.update.Data?> = _responseupdate

    fun updateproduct(nameProduct : RequestBody, price : RequestBody, description : RequestBody, image : MultipartBody.Part, category : RequestBody, token : String, releaseDate : RequestBody, location:RequestBody){
        api.updateProduct(nameProduct, price, description, image, category, token, releaseDate, location).enqueue(object : Callback<com.ilham.etumarketbyseller.model.product.update.Data>{
            override fun onResponse(
                call: Call<com.ilham.etumarketbyseller.model.product.update.Data>,
                response: Response<com.ilham.etumarketbyseller.model.product.update.Data>
            ) {
                if (response.isSuccessful){
                    _responseupdate.postValue(response.body())
                }else{
                    _responseupdate.postValue(null)
                    error(response.message())
                }
            }

            override fun onFailure(
                call: Call<com.ilham.etumarketbyseller.model.product.update.Data>,
                t: Throwable
            ) {
                error(t)
            }

        })

    }



}