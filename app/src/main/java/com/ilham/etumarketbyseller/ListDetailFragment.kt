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
import com.bumptech.glide.Glide
import com.ilham.etumarketbyseller.databinding.FragmentListDetailBinding
import com.ilham.etumarketbyseller.model.product.status.PostUpdateStatus
import com.ilham.etumarketbyseller.viewmodel.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ListDetailFragment : Fragment() {
    lateinit var binding: FragmentListDetailBinding
    lateinit var adminVm: AdminViewModel
    lateinit var pref: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adminVm = ViewModelProvider(this).get(AdminViewModel::class.java)
        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)

        val token = pref.getString("token", "").toString()

        val kodetransaction = arguments?.getString("kodetrans")
        val IdProduct = arguments?.getString("productid")




        binding.btnUpdateStatus.setOnClickListener {


            val selectedStatus = when (binding.rgStatusPesanan.checkedRadioButtonId) {
                R.id.pending -> "Pending"
                R.id.paid -> "Paid"
                R.id.Ondelivery -> "On Delivery"
                R.id.Delivered -> "Delivered"
                R.id.Expired -> "Expired"
                R.id.Failed -> "Failed"
                else -> {
                    // If nothing is selected, show an error message and return
                    Toast.makeText(requireContext(), "Please select a status", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
            }

            val postupdate = PostUpdateStatus(kodetransaction!!, IdProduct!!, selectedStatus)

            adminVm.updateStatus(token, postupdate)

            adminVm.dataupdatestatus.observe(viewLifecycleOwner) {
                if (it.message == "Berhasil update status transaksi") {
                    Toast.makeText(context, "Berhasil Update Status", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Tidak Berhasil Update Status", Toast.LENGTH_LONG).show()
                }
            }
        }



        adminVm.getpesanan(token)

        observeDetailProduct(kodetransaction!!, IdProduct!!)


    }

    private fun observeDetailProduct(kodetransaction: String, productId: String) {
        adminVm.datapesanan.observe(viewLifecycleOwner) { dataTokoList ->
            dataTokoList.forEach { dataToko ->
                dataToko.products.forEach {
                    if (dataToko.kodeTransaksi == kodetransaction) {
                        if (it.productID.id == productId) {
                            // Set nama produk
                            binding.tvNamaproductdetail.text = it.productID.nameProduct
                            // Set nama pembeli
                            binding.tvCategory.text = it.productID.category
                            // Set quantity
                            binding.tvQuantity.text = it.quantity.toString()
                            // Set total harga
                            binding.tvTotalharga.text = it.productID.price.toString()
                            // Load foto produk
                            Glide.with(requireContext())
                                .load("https://7895jr9m-3000.asse.devtunnels.ms/uploads/${it.productID.image}")
                                .into(binding.ivProductimagedetail)
                        }
                    }
                }
            }
                }
            }
        }







