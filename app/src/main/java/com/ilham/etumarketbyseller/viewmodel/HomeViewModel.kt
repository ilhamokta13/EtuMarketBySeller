package com.ilham.etumarketbyseller.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ilham.etumarketbyseller.model.product.allproduct.AllProductResponse
import com.ilham.etumarketbyseller.model.product.allproduct.DataAllProduct
import com.ilham.etumarketbyseller.model.product.delete.DeleteProductResponse
import com.ilham.etumarketbyseller.model.product.getproductadmin.DataAdmin
import com.ilham.etumarketbyseller.model.product.getproductadmin.GetProductAdminResponse
import com.ilham.etumarketbyseller.model.product.productperid.DataPerId
import com.ilham.etumarketbyseller.model.product.productperid.GetProductPerId
import com.ilham.etumarketbyseller.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val api : ApiService) : ViewModel() {

    private val livedeleteproduct : MutableLiveData<DataAdmin> = MutableLiveData()
    val itemdeleteproduct : LiveData<DataAdmin> = livedeleteproduct

    fun deleteproduct(token: String, id:String){
        api.deleteProduct("Bearer $token", id ).enqueue(object : Callback<DeleteProductResponse>{
            override fun onResponse(
                call: Call<DeleteProductResponse>,
                response: Response<DeleteProductResponse>
            ) {
                if (response.isSuccessful){
                    livedeleteproduct.value = response.body()!!.data
                }
                else{
                    Log.e("HomeViewModel", "Cannot send data delete")
                }
            }

            override fun onFailure(call: Call<DeleteProductResponse>, t: Throwable) {
                Log.e("HomeViewModel", "Delete Null")
            }

        })
    }


    private val livedataperid : MutableLiveData<DataPerId> = MutableLiveData()
    val dataproductperid : LiveData<DataPerId> = livedataperid

    fun getproductperid(token: String, id: String){
       api.getProductId("Bearer $token",id).enqueue(object : Callback<GetProductPerId>{
           override fun onResponse(
               call: Call<GetProductPerId>,
               response: Response<GetProductPerId>
           ) {
               if (response.isSuccessful){
                   livedataperid.value = response.body()!!.data
               }
               else{
                   Log.e("HomeViewModel", "Cannot send data get product per id")
               }
           }

           override fun onFailure(call: Call<GetProductPerId>, t: Throwable) {
               Log.e("HomeViewModel", "Data Id Per Null")
           }

       })

    }

    private val livedataproductbyadmin : MutableLiveData<List<DataAdmin>> = MutableLiveData()
    val dataproductbyadmin : LiveData<List<DataAdmin>> = livedataproductbyadmin

    fun getproductbyadmin(token:String){
        api.getproductadmin("Bearer $token").enqueue(object : Callback<GetProductAdminResponse>{
            override fun onResponse(
                call: Call<GetProductAdminResponse>,
                response: Response<GetProductAdminResponse>
            ) {
                if (response.isSuccessful){
                    livedataproductbyadmin.value = response.body()!!.data
                }
                else{
                    Log.e("HomeViewModel", "Cannot send data admin")
                }

            }

            override fun onFailure(call: Call<GetProductAdminResponse>, t: Throwable) {
                Log.e("HomeViewModel", "Data Admin Null")
            }

        })


    }







}