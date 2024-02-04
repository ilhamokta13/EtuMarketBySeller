package com.ilham.etumarketbyseller.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ilham.etumarketbyseller.model.product.allproduct.Data
import com.ilham.etumarketbyseller.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val api : ApiService) : ViewModel() {


    private val liveDataProduct : MutableLiveData<List<Data>> = MutableLiveData()
    val dataProduct : LiveData<List<Data>> = liveDataProduct
    fun getAllproduct(){
        api.getAllProduct().enqueue(object : Callback<List<Data>>{
            override fun onResponse(call: Call<List<Data>>, response: Response<List<Data>>) {
                if (response.isSuccessful){
                    liveDataProduct.postValue(response.body())
                    Log.d("Home", "Get Data")
                }else{
                    Log.e("HomeViewModel", "Data Null")
                }
            }

            override fun onFailure(call: Call<List<Data>>, t: Throwable) {
                Log.e("HomeViewModel", "Cannot get data")
            }

        })
    }





}