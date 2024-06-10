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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ilham.etumarketbyseller.R
import com.ilham.etumarketbyseller.databinding.FragmentDataAdminBinding
import com.ilham.etumarketbyseller.datastore.SettingPreferences
import com.ilham.etumarketbyseller.datastore.ViewModelFactory
import com.ilham.etumarketbyseller.model.product.DataSliderResponse
import com.ilham.etumarketbyseller.viewmodel.AdminViewModel
import com.ilham.etumarketbyseller.viewmodel.SettingViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class DataAdminFragment : Fragment() {

    lateinit var binding : FragmentDataAdminBinding
    lateinit var pref : SharedPreferences
    private lateinit var sliderAdapter:SliderAdapter
    private lateinit var kelasList:ArrayList<DataSliderResponse>
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDataAdminBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)




    }









}