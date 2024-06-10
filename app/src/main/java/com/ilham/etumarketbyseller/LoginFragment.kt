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
import java.util.regex.Pattern

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
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty()) {
                binding.etEmail.error = "Isi Username"
            } else if (!isValidEmail(email)) {
                Toast.makeText(context, "Email salah", Toast.LENGTH_SHORT).show()
            } else if (password.isEmpty()) {
                binding.etPassword.error = "Isi Password"
            } else if (password.length < 6) {
                Toast.makeText(context, "Password kurang dari 6 karakter", Toast.LENGTH_SHORT).show()
            } else {
                login(email,password)
            }
        }

        binding.register.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.lupaPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_resetPassFragment)
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


    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        )
        return emailPattern.matcher(email).matches()
    }

    private fun login(email : String, password:String) {


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
                                    if (response.message == "Email not found") {
                                        Toast.makeText(context, "Email not found", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Wrong password", Toast.LENGTH_SHORT).show()
                                    }
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
                    val exception = task.exception
                    if (exception != null) {
                        if (exception.message?.contains("password") == true) {
                            Toast.makeText(context, "Password salah", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Firebase Authentication Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }







}