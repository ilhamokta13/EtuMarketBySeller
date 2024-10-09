package com.ilham.etumarketbyseller

import android.Manifest
import android.Manifest.permission.POST_NOTIFICATIONS
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ilham.etumarketbyseller.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding : ActivityMainBinding
//    private lateinit var navController: NavController

    lateinit var pref : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = this.getSharedPreferences("Success", Context.MODE_PRIVATE)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
    val navController = navHostFragment.navController
    binding.bottomnav.setupWithNavController(navController)

    binding.bottomnav.setOnItemSelectedListener { item ->

        when(item.itemId){
            R.id.homeFragment -> {
                navController.navigate(R.id.homeFragment)
                true
            }
            R.id.listFragment -> {
                navController.navigate(R.id.listFragment)
                true
            }
            R.id.userFragment -> {
                navController.navigate(R.id.userFragment)
                true
            }

            R.id.profileFragment ->{
                navController.navigate(R.id.profileFragment)
                true
            } else -> {
            false
        }
        }

    }


    navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment, R.id.loginFragment, R.id.registerFragment, R.id.addProductSellerFragment, R.id.pilihanFragment,R.id.editFragment, R.id.loginAdminFragment, R.id.registerAdminFragment, R.id.dataAdminFragment, R.id.homeAdminFragment, R.id.grafikFragment, R.id.listDetailFragment, R.id.userAdminFragment, R.id.profileFragmentAdmin, R.id.detailAdminFragment, R.id.settingFragment, R.id.aboutUsFragment-> {
                    binding.bottomnav.visibility = View.GONE
                }
                else -> {
                    binding.bottomnav.visibility = View.VISIBLE
                }
            }
        }


        if (intent.hasExtra("fragment")) {
            val fragment = intent.getStringExtra("fragment")
            if (fragment == "listFragment") {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, ListFragment())
                    .commit()
            }
        }

        if (intent.hasExtra("navigateTo")) {
            val fragmentId = intent.getIntExtra("navigateTo", -1)
            if (fragmentId != -1) {
                navController.navigate(fragmentId)
            }
        }



        // Check and request notification permission if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }





    }



}