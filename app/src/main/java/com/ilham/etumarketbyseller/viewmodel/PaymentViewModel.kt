package com.ilham.etumarketbyseller.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ilham.etumarketbybuyer.model.transaksi.GetTransaksiResponse
import com.ilham.etumarketbybuyer.model.transaksi.PostTransaction
import com.ilham.etumarketbyseller.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class PaymentViewModel @Inject constructor(private val api : ApiService) : ViewModel() {

    private val liveMidtransResponse: MutableLiveData<GetTransaksiResponse> = MutableLiveData()
    val midtransResponse: LiveData<GetTransaksiResponse> = liveMidtransResponse

    private val _paymentStatus: MutableLiveData<String> = MutableLiveData()
    val paymentStatus: LiveData<String> = _paymentStatus

    fun postpayment(token : String, dataTransaksi: PostTransaction){
        api.posttransaksi("Bearer $token",dataTransaksi).enqueue(object:
            Callback<GetTransaksiResponse> {
            override fun onResponse(
                call: Call<GetTransaksiResponse>,
                response: Response<GetTransaksiResponse>
            ) {
                if (response.isSuccessful) {
                    val status = response.body()?.data?.status
                    if (status == "Paid") {
                        // Update payment status
                        _paymentStatus.value = status!!
                    }
                } else {
                    Log.e("All Transaction", "${response.errorBody()?.string()}")

                }

            }

            override fun onFailure(call: Call<GetTransaksiResponse>, t: Throwable) {
                Log.e("HomeViewModel", "Null Post Data Cart")
            }

        })

    }
}