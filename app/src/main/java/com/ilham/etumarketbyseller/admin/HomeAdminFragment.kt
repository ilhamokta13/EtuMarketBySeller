package com.ilham.etumarketbyseller.admin

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ilham.etumarketbyseller.R
import com.ilham.etumarketbyseller.databinding.FragmentHomeAdminBinding
import com.ilham.etumarketbyseller.datastore.SettingPreferences
import com.ilham.etumarketbyseller.datastore.ViewModelFactory
import com.ilham.etumarketbyseller.viewmodel.AdminViewModel
import com.ilham.etumarketbyseller.viewmodel.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeAdminFragment : Fragment() {
    lateinit var binding : FragmentHomeAdminBinding
    lateinit var pref : SharedPreferences
    lateinit var adminVm : AdminViewModel
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)!!

        adminVm = ViewModelProvider(this).get(AdminViewModel::class.java)

        binding.cardHome.setOnClickListener {
            findNavController().navigate(R.id.action_homeAdminFragment_to_aboutUsFragment)
        }

//        binding.cardChat.setOnClickListener {
//            findNavController().navigate(R.id.action_homeAdminFragment_to_userAdminFragment)
//        }

        binding.cardProfile.setOnClickListener {
            findNavController().navigate(R.id.action_homeAdminFragment_to_profileFragmentAdmin)
        }

        binding.cardGrafik.setOnClickListener {
            findNavController().navigate(R.id.action_homeAdminFragment_to_detailAdminFragment)
        }



        binding.cardSettings.setOnClickListener {
            findNavController().navigate(R.id.action_homeAdminFragment_to_settingFragment)
        }

//        binding.cardLogout.setOnClickListener {
//            val editor = pref.edit()
//            editor.remove("token")
//            editor.apply()
//            findNavController().navigate(R.id.action_homeAdminFragment_to_loginAdminFragment)
//        }

        val preferences = SettingPreferences.getInstance(requireContext().dataStore)
        val settingModel = createSettingViewModel(preferences)

        settingModel.themeSetting.observe(requireActivity()) { isDarkModeActive ->
            updateNightMode(isDarkModeActive)
        }

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

}