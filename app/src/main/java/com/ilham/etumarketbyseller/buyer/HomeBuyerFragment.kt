package com.ilham.etumarketbyseller.buyer

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.ilham.etumarketbyseller.R
import com.ilham.etumarketbyseller.databinding.FragmentHomeBuyerBinding
import com.ilham.etumarketbyseller.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeBuyerFragment : Fragment() {
    lateinit var binding : FragmentHomeBuyerBinding
    lateinit var pref : SharedPreferences
    lateinit var homeVm : HomeViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBuyerBinding.inflate(inflater,container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)!!
        homeVm = ViewModelProvider(this).get(HomeViewModel::class.java)

        getProduct()


    }

    override fun onStart() {
        super.onStart()
        val getUser = pref.getString("fullname", "")
        if (getUser != null){
            binding.textViewwc.text = "Welcome, $getUser"
        }else{
            binding.textViewwc.text = "Welcome"
        }
    }

    fun getProduct(){
       homeVm.getAllproduct()
           homeVm.dataProduct.observe(viewLifecycleOwner, Observer{
               val layoutManager = GridLayoutManager(context,2)
               binding.rvListproducthome.layoutManager = layoutManager
            if (it!= null) {
                binding.rvListproducthome.adapter = BuyerAdapter(it)
            }
        })
    }




}