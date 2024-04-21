package com.ilham.etumarketbyseller

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.ilham.etumarketbyseller.databinding.FragmentLoginBinding
import com.ilham.etumarketbyseller.model.DataRegister
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
        binding = FragmentLoginBinding.inflate(inflater,container,false)
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


        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_pilihanFragment)
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Tidak melakukan apa-apa ketika tombol kembali ditekan
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)



    }

    fun login() {
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Firebase authentication successful
                    binding.etEmail.setText("")
                    binding.etPassword.setText("")

                    // Now, check your custom authentication logic
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        userVm.responselogin.observe(viewLifecycleOwner, Observer { response ->
                            if (response.message == "Success") {
                                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                                Toast.makeText(context, "User Berhasil Login", Toast.LENGTH_SHORT).show()

                                // Save token to SharedPreferences
                                val sharedPref = pref.edit()
                                sharedPref.putString("token", response.token)
                                sharedPref.apply()
                            } else {
                                // Tambahkan toast jika login gagal karena username atau password salah
                                if (response.message == "Invalid email or password") {
                                    Toast.makeText(context, "Username atau Password Salah", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })

                        userVm.postlogin(LoginBody(email, password))
                    } else {
                        Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Firebase authentication failed
                    Toast.makeText(context, "Firebase Authentication Failed", Toast.LENGTH_SHORT).show()
                }
            }




    }







}