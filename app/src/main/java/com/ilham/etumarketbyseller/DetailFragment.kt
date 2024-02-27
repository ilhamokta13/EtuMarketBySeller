package com.ilham.etumarketbyseller

import android.content.Context
import android.content.SharedPreferences
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.ilham.etumarketbyseller.databinding.FragmentDetailBinding
import com.ilham.etumarketbyseller.model.product.getproductadmin.DataAdmin
import com.ilham.etumarketbyseller.viewmodel.HomeViewModel
import com.ilham.etumarketbyseller.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.*

@AndroidEntryPoint
class DetailFragment : Fragment() {
    lateinit var binding : FragmentDetailBinding
    lateinit var pref : SharedPreferences
    lateinit var homeVm : HomeViewModel
    lateinit var productVm : ProductViewModel
    private lateinit var idProduct: String


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)!!
        homeVm = ViewModelProvider(this).get(HomeViewModel::class.java)
        productVm = ViewModelProvider(this).get(ProductViewModel::class.java)

        val getData = arguments?.getSerializable("detail") as DataAdmin
        val token = pref.getString("token", "").toString()
//        val getId = pref.getString("id", " ")
        idProduct = getData.id
        homeVm.getproductperid(token,idProduct)
        observeDetailProduct()

        val fullname = pref.getString("fullname", "")
        binding.welcome.text = "Welcome, $fullname!"
        Log.d("Homescreen", "Username : $fullname")



    }

    private fun observeDetailProduct() {
        homeVm.dataproductperid.observe(viewLifecycleOwner) { detailproduct ->
            binding.apply {
                if (detailproduct != null) {
                    binding.tvNamaproductdetail.text = detailproduct.nameProduct
                    binding.tvHargaproduk.text = detailproduct.price.toString()
                    binding.tvDescription.text = detailproduct.description
                    binding.tvCategory.text = detailproduct.category
                    binding.tvRelease.text = detailproduct.releaseDate

                    val lon = detailproduct.longitude
                    val lat = detailproduct.latitude

                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    try {
                        val addresses: List<Address> = geocoder.getFromLocation(lon, lat, 1) as List<Address>
                        if (addresses.isNotEmpty()) {
                            val locName: String = addresses[0].getAddressLine(0)
                            binding.tvLocation.text = locName
                            productVm.saveLocation(locName)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(context, "Alamat Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
                }

                    Glide.with(requireContext())
                        .load("https://7895jr9m-3000.asse.devtunnels.ms/uploads/${detailproduct.image}")
                        .into(binding.ivProductimagedetail)


                }



            }
        }


    }


