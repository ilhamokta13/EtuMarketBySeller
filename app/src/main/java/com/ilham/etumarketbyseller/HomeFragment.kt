package com.ilham.etumarketbyseller

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ilham.etumarketbyseller.databinding.FragmentHomeBinding
import com.ilham.etumarketbyseller.viewmodel.HomeViewModel
import com.ilham.etumarketbyseller.viewmodel.ProductViewModel
import com.ilham.etumarketbyseller.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    lateinit var pref: SharedPreferences
    lateinit var homeVm: HomeViewModel
    lateinit var productVm: ProductViewModel
    lateinit var userVm : UserViewModel
    private lateinit var selleradapter: SellerAdapter



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)!!
        homeVm = ViewModelProvider(this).get(HomeViewModel::class.java)
        productVm = ViewModelProvider(this).get(ProductViewModel::class.java)
        userVm = ViewModelProvider(this).get(UserViewModel::class.java)
//        val fullname = pref.getString("fullname", "")
//        binding.welcome.text = "Welcome, $fullname!"
//        Log.d("Homescreen", "Username : $fullname")
        val token = pref.getString("token", "").toString()
        selleradapter = SellerAdapter(requireContext(),ArrayList(),homeVm, productVm)


        userVm.getprofile(token)
        userVm.dataprofile.observe(viewLifecycleOwner) {
            val fullname  = it.fullName
         binding.welcome.text = "Welcome , $fullname"
        }

        getdata(token)



        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addProductSellerFragment)

        }

        binding.btnLogout.setOnClickListener {
            val editor = pref.edit()
            editor.remove("token")
            editor.apply()
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }

//        postdata(id)


//        adapter = SellerAdapter(ArrayList())
//
//        binding.rvMain.apply {
//            setHasFixedSize(true)
//            layoutManager = LinearLayoutManager(context)
//        }
//
//        homeVm.dataProduct.observe(viewLifecycleOwner) {
//            if (it != null) {
//                binding.rvMain.adapter = SellerAdapter(it)
//
//
//            }
//        }

//        homeVm.getAllproduct()

//        binding.swipeRefreshLayout.setOnRefreshListener {
//
//            getdata(token)
//
//            Handler().postDelayed({
//                binding.swipeRefreshLayout.isRefreshing = false
//            }, 2000)
//        }
//
//        simulateAutomaticRefresh()

        binding.etSearchProduct.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                selleradapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })




    }




    fun getdata(token: String) {
        homeVm = ViewModelProvider(this).get(HomeViewModel::class.java)

        homeVm.getproductbyadmin(token)

        homeVm.dataproductbyadmin.observe(viewLifecycleOwner, Observer {
            selleradapter.filteredList = it
            selleradapter.listproduct= it

            binding.rvMain.layoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL, false
            )

                binding.rvMain.adapter = selleradapter



        })
    }











//    fun postdata(id:String){
//        homeVm = ViewModelProvider(this).get(HomeViewModel::class.java)
//
//        homeVm.getproductperid(id)
//
//        homeVm.dataproductperid.observe(viewLifecycleOwner , Observer {
//
//
//        })
//
//
//    }



}
