package com.ilham.etumarketbyseller

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.ilham.etumarketbyseller.databinding.FragmentPostTawarHargaBinding
import com.ilham.etumarketbyseller.viewmodel.ProductViewModel
import com.ilham.etumarketbyseller.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PostTawarHargaFragment : Fragment() {

    lateinit var binding : FragmentPostTawarHargaBinding
    lateinit var pref: SharedPreferences
    lateinit var productVm: ProductViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPostTawarHargaBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productVm = ViewModelProvider(this).get(ProductViewModel::class.java)
        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)
        val token = pref.getString("token", "").toString()

        val IdProduct = arguments?.getString("keytawar")
        val offerId = arguments?.getString("offerId")

        binding.btnUpdateStatus.setOnClickListener {

            val selectedStatus = when (binding.rgStatusPesanan.checkedRadioButtonId) {
                R.id.accepted -> "accepted"
                R.id.rejected -> "rejected"
                else -> null

            }

            productVm.confirmtawarharga(token,IdProduct!!,offerId!!, selectedStatus!!)
            productVm.dataconfirmstatusharga.observe(viewLifecycleOwner){
                if (it.message == "Offer status updated successfully") {
                    Toast.makeText(context, "Berhasil Update Status", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Tidak Berhasil Update Status", Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}