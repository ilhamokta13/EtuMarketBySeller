package com.ilham.etumarketbyseller.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ilham.etumarketbyseller.model.pendapatan.DataToko
import com.ilham.etumarketbyseller.model.pendapatan.GetPendapatanToko
import com.ilham.etumarketbyseller.model.product.listpesanan.DataPesanan
import com.ilham.etumarketbyseller.model.product.listpesanan.GetPesananResponse
import com.ilham.etumarketbyseller.model.product.status.ResponseUpdateStatus
import com.ilham.etumarketbyseller.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(private val api : ApiService) : ViewModel()  {



    private val liveDataPesanan : MutableLiveData<List<DataPesanan>> = MutableLiveData()
    val datapesanan : LiveData<List<DataPesanan>> = liveDataPesanan

    fun getpesanan(token: String){
        api.gethistory("Bearer $token").enqueue(object : Callback<GetPesananResponse>{
            override fun onResponse(
                call: Call<GetPesananResponse>,
                response: Response<GetPesananResponse>
            ) {
                if (response.isSuccessful) {
                    val historyList = mutableListOf<DataPesanan>()
                    response.body()?.data?.forEach { transaction ->
                        // Memproses setiap transaksi untuk menambahkannya ke daftar riwayat
                        transaction.products.forEach{ product->
                            // Membuat objek DataHistory baru untuk setiap produk dalam transaksi
                            val dataHistory = DataPesanan(
                                transaction.destination,
                                transaction.kodeTransaksi,
                                listOf(product),
                                transaction.shippingCost,
                                transaction.status,
                                transaction.total,// Menggunakan listOf(product) untuk menambahkan produk ke dalam list products
                                transaction.transaksiId,
                                transaction.user
                                      // Memastikan properti __v juga disertakan jika diperlukan
                            )
                            // Menambahkan objek DataHistory ke daftar riwayat
                            historyList.add(dataHistory)
                        }
                    }
                    liveDataPesanan.value = historyList
                } else {
                    Log.e("UserViewModel", "${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<GetPesananResponse>, t: Throwable) {
                Log.e("HistoryViewModel2", "Null Post Data Cart")

            }

        })
    }

    private val liveUpdateStatus : MutableLiveData<ResponseUpdateStatus> = MutableLiveData()
    val dataupdatestatus : LiveData<ResponseUpdateStatus> = liveUpdateStatus

    fun updateStatus(token: String, kode_transaksi:String,productID:String,status:String) {
        api.poststatus("Bearer $token",kode_transaksi, productID, status).enqueue(object : Callback<ResponseUpdateStatus>{
            override fun onResponse(
                call: Call<ResponseUpdateStatus>,
                response: Response<ResponseUpdateStatus>
            ) {
                if (response.isSuccessful) {
                    liveUpdateStatus.value = response.body()
                } else {
                    Log.e("AdminViewMo2", "${response.errorBody()?.string()}")

                }
            }

            override fun onFailure(call: Call<ResponseUpdateStatus>, t: Throwable) {
                Log.e("AdminViewMo", "Null Post Update Status")
            }

        })
    }

    fun updateStatusimage(token: String, image : MultipartBody.Part) {
        api.poststatusimage("Bearer $token", image).enqueue(object : Callback<ResponseUpdateStatus>{
            override fun onResponse(
                call: Call<ResponseUpdateStatus>,
                response: Response<ResponseUpdateStatus>
            ) {
                if (response.isSuccessful) {
                    liveUpdateStatus.value = response.body()
                } else {
                    Log.e("AdminViewMo2", "${response.errorBody()?.string()}")

                }
            }

            override fun onFailure(call: Call<ResponseUpdateStatus>, t: Throwable) {
                Log.e("AdminViewMo", "Null Post Update Status")
            }

        })
    }

    private val livePendapatanToko : MutableLiveData<List<DataToko>> = MutableLiveData()
    val datapendapatanToko: LiveData<List<DataToko>> = livePendapatanToko

    fun DatapendapatanToko(token: String){
        api.getpendapatan("Bearer $token").enqueue(object : Callback<GetPendapatanToko>{
            override fun onResponse(
                call: Call<GetPendapatanToko>,
                response: Response<GetPendapatanToko>
            ) {
                if (response.isSuccessful) {
                    livePendapatanToko.value = response.body()!!.data
                } else {
                    Log.e("AdminViewModel2", "${response.errorBody()?.string()}")

                }
            }

            override fun onFailure(call: Call<GetPendapatanToko>, t: Throwable) {
                Log.e("AdminViewMo3", "Null Get Pendapatan Toko")
            }

        })
    }



}