package com.ilham.etumarketbyseller.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ilham.etumarketbyseller.model.ResponseRegister
import com.ilham.etumarketbyseller.model.login.LoginBody
import com.ilham.etumarketbyseller.model.login.ResponseLogin
import com.ilham.etumarketbyseller.model.profile.DataProfile
import com.ilham.etumarketbyseller.model.profile.UpdateProfileResponse
import com.ilham.etumarketbyseller.model.profile.allprofile.AllProfileResponse
import com.ilham.etumarketbyseller.model.profile.allprofile.Data
import com.ilham.etumarketbyseller.model.profile.profilebyid.ProfilebyIdResponse
import com.ilham.etumarketbyseller.model.profile.profilebyid.UserProfile
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


    private val _responseupdateprofile : MutableLiveData<DataProfile> = MutableLiveData()
    val responseupdateprofile : LiveData<DataProfile> = _responseupdateprofile

    fun updateprofile(token: String, fullName: String, email: String, telp: String, role: String, shopName:String){
        api.updateprofile("Bearer $token", fullName, email, telp, role, shopName).enqueue(object : Callback<UpdateProfileResponse>{
            override fun onResponse(
                call: Call<UpdateProfileResponse>,
                response: Response<UpdateProfileResponse>
            ) {
                if (response.isSuccessful) {
                    _responseupdateprofile.value = response.body()!!.data

                } else {
                    Log.e("Update Profile", "${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<UpdateProfileResponse>, t: Throwable) {
                Log.e("Update Profile Null", "Cannot get data update profile")
            }

        })

    }


    fun setDataMessage(){
        _toastLogin.value=null
    }

    private val _responseallprofile : MutableLiveData<List<Data>> = MutableLiveData()
    val responseallprofile : LiveData<List<Data>> = _responseallprofile

    fun allprofile(token: String){
        api.getallprofile("Bearer $token").enqueue(object : Callback<AllProfileResponse>{
            override fun onResponse(
                call: Call<AllProfileResponse>,
                response: Response<AllProfileResponse>
            ) {
                if (response.isSuccessful) {
                    _responseallprofile.value = response.body()!!.data

                } else {
                    Log.e("DataAllProfile", "${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<AllProfileResponse>, t: Throwable) {
                Log.e("DataAllProfileNull", "CannotAllDataProfile")
            }

        })
    }

    private val getprofile : MutableLiveData<UserProfile> = MutableLiveData()
    val dataprofile : LiveData<UserProfile> = getprofile

    fun getprofile(token: String){
        api.getprofile("Bearer $token").enqueue(object : Callback<ProfilebyIdResponse>{
            override fun onResponse(
                call: Call<ProfilebyIdResponse>,
                response: Response<ProfilebyIdResponse>
            ) {
                if (response.isSuccessful) {
                    getprofile.value = response.body()!!.data

                } else {
                    Log.e("GetProfile", "${response.errorBody()?.string()}")
                }

            }

            override fun onFailure(call: Call<ProfilebyIdResponse>, t: Throwable) {
                Log.e("GetProfile", "Cannot Get Data Profile User")
            }

        })
    }




}