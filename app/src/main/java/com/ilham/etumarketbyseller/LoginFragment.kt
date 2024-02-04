package com.ilham.etumarketbyseller

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.ilham.etumarketbyseller.databinding.FragmentLoginBinding
import com.ilham.etumarketbyseller.model.login.LoginBody
import com.ilham.etumarketbyseller.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private lateinit var binding : FragmentLoginBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var pref: SharedPreferences
    private lateinit var userVm : UserViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)
        userVm = ViewModelProvider(this).get(UserViewModel::class.java)


        binding.login.setOnClickListener {
            if (binding.etEmail.text.toString().isEmpty()) {
                binding.etEmail.setError("Isi Username")
            } else if (binding.etPassword.text.toString().isEmpty()) {
                binding.etPassword.setError("Isi Password")
            } else if (binding.etPassword.text.toString().length < 6) {
                Toast.makeText(context, "Password kurang dari 6 karakter", Toast.LENGTH_SHORT).show()
            }
            else {
                login()


            }
        }

        binding.register.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_loginFragment_to_registerFragment)
        }



    }

    fun login() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()){
            userVm.responselogin.observe(viewLifecycleOwner, Observer {
//            listuserlogin = it
//            loginAuth(listuserlogin)
                if(it.message == "Success"){
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    Toast.makeText(context, "User Berhasil Login", Toast.LENGTH_SHORT).show()


                }else{
                    Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                }
                val sharedPref = pref.edit()
                sharedPref.putString("token", it.token)
                sharedPref.apply()

            })
            userVm.postlogin(LoginBody(email, password))

        } else{
            Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
        }




    }


}