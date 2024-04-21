package com.ilham.etumarketbyseller.admin

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.ilham.etumarketbyseller.R
import com.ilham.etumarketbyseller.databinding.FragmentProfileAdminBinding
import com.ilham.etumarketbyseller.databinding.FragmentProfileBinding
import com.ilham.etumarketbyseller.datastore.SettingPreferences
import com.ilham.etumarketbyseller.datastore.ViewModelFactory
import com.ilham.etumarketbyseller.model.profile.DataProfile
import com.ilham.etumarketbyseller.viewmodel.SettingViewModel
import com.ilham.etumarketbyseller.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProfileFragmentAdmin : Fragment() {

    lateinit var binding: FragmentProfileAdminBinding
    lateinit var pref: SharedPreferences
    lateinit var userVm: UserViewModel
    private lateinit var adapter: TabelAdapter
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileAdminBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)!!
        userVm = ViewModelProvider(this).get(UserViewModel::class.java)
        val token = pref.getString("token","").toString()

        userVm.allprofile(token)

        adapter = TabelAdapter(requireContext(), ArrayList())

        // Atur adapter pada ListView
        binding.listPertamina.adapter = adapter

        observeData()

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

    private fun observeData() {
        // Observe the profile data from the ViewModel
        userVm.responseallprofile.observe(viewLifecycleOwner, { profileList ->
            // profileList contains the actual data, not the LiveData wrapper
            if (profileList != null) {
                // Update adapter data if response is successful
                adapter.clear()
                adapter.addAll(profileList)
                adapter.notifyDataSetChanged()
            } else {
                // Handle errors if any
                Toast.makeText(requireContext(), "Failed to get profile data", Toast.LENGTH_SHORT).show()
            }
        })
    }






}