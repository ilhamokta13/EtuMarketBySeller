package com.ilham.etumarketbyseller.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ilham.etumarketbyseller.model.ResponseRegister
import com.ilham.etumarketbyseller.model.login.LoginBody
import com.ilham.etumarketbyseller.model.login.ResponseLogin
import com.ilham.etumarketbyseller.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val api : ApiService) : ViewModel() {

    private val _responseregister : MutableLiveData<ResponseRegister> = MutableLiveData()
    val responseregister : LiveData<ResponseRegister> = _responseregister

    private val _toastLogin = MutableLiveData<String?>()
    val toastMessage: LiveData<String?> = _toastLogin

    private val _responselogin : MutableLiveData<ResponseLogin> = MutableLiveData()
    val responselogin : LiveData<ResponseLogin> = _responselogin

    fun postregist(fullName : String, email : String, password : String, telp : String, role : String,){
        api.register(fullName, email, password, telp, role,).enqueue(object : Callback<ResponseRegister>{
            override fun onResponse(
                call: Call<ResponseRegister>,
                response: Response<ResponseRegister>
            ) {
                if (response.isSuccessful) {
                    _responseregister.value = response.body()

                } else {
                    Log.e("UserViewModel", "Cannot get data")
                }
            }

            override fun onFailure(call: Call<ResponseRegister>, t: Throwable) {
                Log.e("UserViewModel", "Cannot get data")
            }

        })

    }

    fun postlogin(loginBody: LoginBody){
        api.login(loginBody).enqueue(object:Callback<ResponseLogin>{
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                if (response.isSuccessful) {
                    _responselogin.value = response.body()

                }
                else {
//                   _responselogin.value = LoginResponse(
//                       "",
//                       "",
//                       false
//                   )
//
                    _toastLogin.value="Login Failed"
                    Log.e("UserViewModel", "Cannot get data")
                }
            }

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                Log.e("UserViewModel", "Cannot get data")
            }

        })
    }


    fun setDataMessage(){
        _toastLogin.value=null
    }


}