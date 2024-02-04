package com.ilham.etumarketbyseller

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ilham.etumarketbyseller.databinding.FragmentHomeBinding
import com.ilham.etumarketbyseller.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private  lateinit var binding : FragmentHomeBinding
    lateinit var pref : SharedPreferences
    lateinit var homeVm : HomeViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)!!
        homeVm = ViewModelProvider(this).get(HomeViewModel::class.java)
        val fullname = pref.getString("fullname", "")
        binding.welcome.text = "Welcome, $fullname!"
        Log.d("Homescreen", "Username : $fullname")

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addProductSellerFragment)

        }

        postdata()






    }


    fun postdata(){
        homeVm = ViewModelProvider(this).get(HomeViewModel::class.java)

        homeVm.getAllproduct()

        homeVm.dataProduct.observe(viewLifecycleOwner , Observer {
            binding.rvMain.layoutManager = LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL,false)
            binding.rvMain.adapter = SellerAdapter(it)
        })


    }



}