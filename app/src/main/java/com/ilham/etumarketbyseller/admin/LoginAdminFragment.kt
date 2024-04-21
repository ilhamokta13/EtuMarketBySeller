package com.ilham.etumarketbyseller.admin

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.ilham.etumarketbyseller.R
import com.ilham.etumarketbyseller.databinding.FragmentLoginAdminBinding
import com.ilham.etumarketbyseller.datastore.SettingPreferences
import com.ilham.etumarketbyseller.datastore.ViewModelFactory
import com.ilham.etumarketbyseller.model.login.LoginBody
import com.ilham.etumarketbyseller.viewmodel.SettingViewModel
import com.ilham.etumarketbyseller.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginAdminFragment : Fragment() {
    lateinit var binding : FragmentLoginAdminBinding
    lateinit var pref: SharedPreferences
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userVm : UserViewModel
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginAdminBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)
        firebaseAuth = FirebaseAuth.getInstance()
        userVm = ViewModelProvider(this).get(UserViewModel::class.java)


        val preferences = SettingPreferences.getInstance(requireContext().dataStore)
        val settingModel = createSettingViewModel(preferences)

        settingModel.themeSetting.observe(requireActivity()) { isDarkModeActive ->
            updateNightMode(isDarkModeActive)
        }

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
            findNavController().navigate(R.id.action_loginAdminFragment_to_registerAdminFragment)
        }

        binding.ivBack.setOnClickListener {
            findNavController().navigate(R.id.action_loginAdminFragment_to_pilihanFragment)
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Tidak melakukan apa-apa ketika tombol kembali ditekan
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }


    private fun createSettingViewModel(pref: SettingPreferences): SettingViewModel {
        return ViewModelProvider(this, ViewModelFactory(pref))[SettingViewModel::class.java]
    }


    private fun updateNightMode(isDarkModeActive: Boolean) {
        val nightMode = if (isDarkModeActive) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
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
                                findNavController().navigate(R.id.action_loginAdminFragment_to_homeAdminFragment)
                                Toast.makeText(context, "User Berhasil Login", Toast.LENGTH_SHORT).show()

                                // Save token to SharedPreferences
                                val sharedPref = pref.edit()
                                sharedPref.putString("token", response.token)
                                sharedPref.apply()
                            } else {
                                Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
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


