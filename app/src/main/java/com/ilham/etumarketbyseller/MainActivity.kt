package com.ilham.etumarketbyseller

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ilham.etumarketbyseller.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
        navController = navHostFragment!!.findNavController()

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment, R.id.loginFragment, R.id.registerFragment, R.id.addProductSellerFragment-> {
                    binding.bottomnav.visibility = View.GONE
                }
                R.id.homeFragment, R.id.listFragment, R.id.chatFragment, R.id.profileFragment -> {
                    binding.bottomnav.visibility = View.VISIBLE
                }
                else -> {
                    binding.bottomnav.visibility = View.VISIBLE
                }
            }
        }



        binding.bottomnav.setupWithNavController(navController)

    }
}