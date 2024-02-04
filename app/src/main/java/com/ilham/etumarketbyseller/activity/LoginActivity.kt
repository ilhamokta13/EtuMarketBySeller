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
import com.ilham.etumarketbyseller.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding:ActivityLoginBinding
    lateinit var firebaseAuth: FirebaseAuth
    lateinit var pref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        pref = this.getSharedPreferences("Success", Context.MODE_PRIVATE)

        binding.login.setOnClickListener {
            login()
        }

        binding.register.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


    }

    fun login() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passEditText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()){
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(this,"Login Berhasil", Toast.LENGTH_LONG).show()
                    val intent = Intent (this, HomeActivity::class.java)
                    startActivity(intent)
                }else{
                    Toast.makeText(this, "Coba Cek Email dan password kembali", Toast.LENGTH_LONG).show()
                }
            }
        }


    }
}