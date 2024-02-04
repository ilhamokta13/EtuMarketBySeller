package com.ilham.etumarketbyseller.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.fragment.findNavController
import com.ilham.etumarketbyseller.R
import com.ilham.etumarketbyseller.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {
    lateinit var binding : ActivityHomeBinding
    lateinit var pref : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = this.getSharedPreferences("Success", Context.MODE_PRIVATE)!!
        val fullname = pref.getString("username", "username")
        binding.welcome.text = "Welcome, $fullname!"
        Log.d("Homescreen", "Username : $fullname")

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent (this, AddProductActivity::class.java)
            startActivity(intent)

        }
    }
}