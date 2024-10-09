package com.ilham.etumarketbyseller

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.ilham.etumarketbyseller.databinding.FragmentListDetailBinding
import com.ilham.etumarketbyseller.model.product.status.PostUpdateStatus
import com.ilham.etumarketbyseller.viewmodel.AdminViewModel
import com.ilham.etumarketbyseller.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*

@AndroidEntryPoint
class ListDetailFragment : Fragment() {
    lateinit var binding: FragmentListDetailBinding
    lateinit var adminVm: AdminViewModel
    private var imageMultipart: MultipartBody.Part? = null
    lateinit var pref: SharedPreferences
    lateinit var userVm :UserViewModel
    private var imageUri: Uri? = Uri.EMPTY
    private var imageFile: File? = null
    private var titleAd:String? = null


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
        userVm = ViewModelProvider(this).get(UserViewModel::class.java)
        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)

        val token = pref.getString("token", "").toString()

        val kodetransaction = arguments?.getString("kodetrans")
        val IdProduct = arguments?.getString("productid")


        binding.uploadImage.setOnClickListener {
//           openGallery()
            getContent.launch("image/*")

        }


        binding.btnUpdateStatus.setOnClickListener {
            val selectedStatus = when (binding.rgStatusPesanan.checkedRadioButtonId) {
                R.id.dikemas -> "Dikemas"
                R.id.dikirim -> "Dikirim"
                R.id.selesai -> "Selesai"
                R.id.dibatalkan -> "Dibatalkan"
                else -> null
            }

            if (selectedStatus == null) {
                Toast.makeText(requireContext(), "Status harus dipilih", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (imageMultipart == null) {
                Toast.makeText(requireContext(), "Harap sertakan foto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            adminVm.updateStatus(token, kodetransaction!!, IdProduct!!, selectedStatus)
            adminVm.updateStatusimage(token, imageMultipart!!)

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

        userVm.dataprofile.observe(viewLifecycleOwner) {
            val fullname  = it.fullName
            binding.welcome.text = "Welcome , $fullname"
        }


    }

    private fun observeDetailProduct(kodetransaction: String, productId: String) {
        adminVm.datapesanan.observe(viewLifecycleOwner) { dataTokoList ->
            dataTokoList.forEach { dataToko ->
                dataToko.products.forEach {
                    if (dataToko.kodeTransaksi == kodetransaction) {
                        if (it.productID.id == productId) {
                            // Set nama produk
                            binding.tvNamaproductdetail.text = "Nama Produk : ${it.productID.nameProduct}"
                            // Set nama pembeli
                            binding.tvCategory.text = "Kategori : ${it.productID.category}"
                            // Set quantity
                            binding.tvQuantity.text = "Jumlah Barang : ${it.quantity}"
                            // Set total harga
                            val harga = it.productID.price
                            val quantity = it.quantity
                            val totalharga = harga * quantity
                            binding.tvTotalharga.text = "Total Harga : $totalharga"
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


    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                val contentResolver: ContentResolver = requireContext().contentResolver
                val type = contentResolver.getType(it)
                imageUri = it

                val fileNameimg = "${System.currentTimeMillis()}.png"
                binding.uploadImage.setImageURI(it)
                Toast.makeText(context, "$imageUri", Toast.LENGTH_SHORT).show()

                val tempFile = File.createTempFile("and1-", fileNameimg, null)
                imageFile = tempFile
                val inputstream = contentResolver.openInputStream(uri)
                tempFile.outputStream().use { result ->
                    inputstream?.copyTo(result)
                }
                val requestBody: RequestBody = tempFile.asRequestBody(type?.toMediaType())
                imageMultipart =
                    MultipartBody.Part.createFormData("image", tempFile.name, requestBody)
            }


        }
        }







