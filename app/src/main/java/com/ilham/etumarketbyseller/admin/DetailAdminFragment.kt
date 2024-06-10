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
import com.ilham.etumarketbyseller.SellerAdapter
import com.ilham.etumarketbyseller.databinding.FragmentDetailAdminBinding
import com.ilham.etumarketbyseller.datastore.SettingPreferences
import com.ilham.etumarketbyseller.datastore.ViewModelFactory
import com.ilham.etumarketbyseller.model.pendapatan.DataToko
import com.ilham.etumarketbyseller.viewmodel.AdminViewModel
import com.ilham.etumarketbyseller.viewmodel.HomeViewModel
import com.ilham.etumarketbyseller.viewmodel.SettingViewModel
import com.ilham.etumarketbyseller.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailAdminFragment : Fragment() {
    lateinit var binding : FragmentDetailAdminBinding
    lateinit var adminVm : AdminViewModel
    lateinit var userVm : UserViewModel
    lateinit var pref: SharedPreferences
    private lateinit var adminAdapter: AdminAdapter
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)!!
        adminVm = ViewModelProvider(this).get(AdminViewModel::class.java)

        userVm = ViewModelProvider(this).get(UserViewModel::class.java)

        val token = pref.getString("token", "").toString()

        adminAdapter = AdminAdapter(ArrayList())

        userVm.getprofile(token)
        userVm.dataprofile.observe(viewLifecycleOwner) {
            val fullname  = it.fullName
            binding.welcome.text = "Welcome , $fullname"
        }





        binding.btnLogout.setOnClickListener {
            val editor = pref.edit()
            editor.remove("token")
            editor.apply()
            findNavController().navigate(R.id.action_detailAdminFragment_to_loginAdminFragment)
        }

        binding.btnGrafik.setOnClickListener {
            findNavController().navigate(R.id.action_detailAdminFragment_to_grafikFragment)
        }

        getdata(token)

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

    fun getdata(token: String) {


        adminVm.DatapendapatanToko(token)

        adminVm.datapendapatanToko.observe(viewLifecycleOwner, Observer {
            binding.EtuPays.text = "ETU Pays : ${calculateTotalPendapatan(it)}"


            binding.rvMain.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false
            )

            if (it!= null) {
                binding.rvMain.adapter = AdminAdapter(it)
            }



        })
    }

    fun calculateTotalPendapatan(cartItems: List<DataToko>?): Int {
        return cartItems?.sumBy { it.totalPendapatan} ?: 0
    }





}