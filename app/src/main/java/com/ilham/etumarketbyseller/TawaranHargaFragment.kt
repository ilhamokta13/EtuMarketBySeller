package com.ilham.etumarketbyseller

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.ilham.etumarketbyseller.databinding.FragmentTawaranHargaBinding
import com.ilham.etumarketbyseller.model.product.tawarharga.TawarProduct
import com.ilham.etumarketbyseller.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TawaranHargaFragment : Fragment() {
   lateinit var binding : FragmentTawaranHargaBinding
    lateinit var pref: SharedPreferences
    lateinit var productVm: ProductViewModel
    private lateinit var tawaranhargaadapter: TawaranHargaAdapter
    private var itemtawar: List<TawarProduct> = listOf()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTawaranHargaBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)!!
        productVm = ViewModelProvider(this).get(ProductViewModel::class.java)
        val token = pref.getString("token", "").toString()
        tawaranhargaadapter = TawaranHargaAdapter(itemtawar)

        val getIdproducts = arguments?.getString("idtawar")

        productVm.tawarharga(token,getIdproducts!!)

        productVm.datatawarharga.observe(viewLifecycleOwner){
            binding.rvMain.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false
            )

            if (it!= null) {
                binding.rvMain.adapter = TawaranHargaAdapter(it)
            }
        }


    }


}