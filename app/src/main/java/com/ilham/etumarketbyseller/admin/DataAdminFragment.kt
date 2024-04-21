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

        kelasList = ArrayList()
        kelasList.add(DataSliderResponse(R.drawable.logo_etu))
        kelasList.add(DataSliderResponse(R.drawable.polinemalogo))

        binding.viewPager.apply {
            sliderAdapter = SliderAdapter(kelasList)
            adapter = sliderAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            binding.dotIndicatorSlide.setViewPager2(binding.viewPager)
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        binding.contentAboutTwo.setText("Entrepreneurship Training Unit / ETU berlokasi di Politeknik Negeri Malang adalah sebuah unit pengelola kewirausahaan yang bertugas memfasilitasi semua kegiatan kewirausahaaan mahasiswa yang terdiri pendidikan, pelatihan, seminar, pemberian modal usaha, konsultasi hingga pendampingan usaha. ETU Polinema berdiri pada 23 Agustus 2006. Seiring dengan berjalannya waktu, kegiatan yang dikelola ETU semakin luas. Saat ini ETU juga mengelola posdaya, menjadi konsultan UMKM binaan posdaya polinema dan bekerja sama dengan beberapa institusi pemerintah untuk memberikan pelatihan. ETU polinema memiliki visi dan misi\n" +
                "Visi :\n" +
                "Mengembangkan lembaga pengembangan kewirausahaan terkemuka di Jawa Timur.\n" +
                "Misi :\n" +
                "1.\tMengembangkan dan meningkatkan pelayanan pelatihan kewirausahaan yang berkesinambungan.\n" +
                "2.\tMengembangkan dan meningkatkan pelayanan pelatihan bisnis yang berkesinambungan.\n" +
                "3.\tMengumpulkan dan menyediakan data dan informasi untuk perluasan jaringan kewirausahaan dan bisnis")

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