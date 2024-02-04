package com.ilham.etumarketbyseller.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.ilham.etumarketbyseller.R
import com.ilham.etumarketbyseller.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    lateinit var binding:ActivityRegisterBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var pref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        pref = this.getSharedPreferences("Success", Context.MODE_PRIVATE)
        binding.btnRegister.setOnClickListener {
            register()
        }
    }

    fun register() {
        val username = binding.usernameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val pass = binding.passEditText.text.toString()
        val confirmpass = binding.confirmpassEditText.text.toString()
        val addAkun = pref.edit()
        addAkun.putString("username", username)



        if (username.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty() && confirmpass.isNotEmpty()) {
            if (pass == confirmpass) {
                addAkun.apply()
                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent (this, LoginActivity::class.java)
                        startActivity(intent)

                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }

            } else {
                Toast.makeText(this, "Password Tidak Sesuai", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Maaf Data Belum Lengkap", Toast.LENGTH_SHORT).show()
        }
    }
}