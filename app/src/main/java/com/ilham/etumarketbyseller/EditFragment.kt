package com.ilham.etumarketbyseller

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ilham.etumarketbyseller.databinding.FragmentEditBinding
import com.ilham.etumarketbyseller.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File


@AndroidEntryPoint
class EditFragment : Fragment() {
    lateinit var binding : FragmentEditBinding
    lateinit var pref: SharedPreferences
    private lateinit var productVm: ProductViewModel
    private var imageMultipart: MultipartBody.Part? = null
    private var imageUri: Uri? = Uri.EMPTY
    private var imageFile: File? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)
        productVm = ViewModelProvider(this).get(ProductViewModel::class.java)

        val getnamaproduk = pref.getString("namaproduk", "")
        binding.uploadNamaProduk.setText(getnamaproduk)

        val getharga = pref.getString("harga", "")
        binding.uploadHargaMenu.setText(getharga)

        val getdeskripsi = pref.getString("deskripsi", "")
        binding.uploadDesc.setText(getdeskripsi)

        val getcategori = pref.getString("categori", "")
        binding.uploadCategory.setText(getcategori)

        val getrelease = pref.getString("release", "")
        binding.uploadreleaseDate.setText(getrelease)

        val getlocation = pref.getString("location", "")
        binding.uploadlocation.setText(getlocation)

        val getimage = pref.getString("image", "")
        binding.uploadImage.setImageResource(getimage.toString().toInt())


        binding.uploadImage.setOnClickListener {
//           openGallery()
            getContent.launch("image/*")

        }

        binding.saveButton.setOnClickListener {
           updatedata()

        }








    }

    fun updatedata(){
        val namaproduk = binding.uploadNamaProduk.text.toString().toRequestBody("text/plain".toMediaType())
        val harga = binding.uploadHargaMenu.text.toString().toRequestBody("text/plain".toMediaType())
        val deskripsi =
            binding.uploadDesc.text.toString().toRequestBody("text/plain".toMediaType())
        val categori = binding.uploadCategory.text.toString().toRequestBody("text/plain".toMediaType())
        val release = binding.uploadreleaseDate.text.toString().toRequestBody("text/plain".toMediaType())
        val location = binding.uploadlocation.text.toString().toRequestBody("text/plain".toMediaType())
        val token  =  pref.getString("token", "").toString()

        productVm.updateproduct(namaproduk,harga,deskripsi,imageMultipart!!, categori,token,release,location)

        productVm.responseupdate.observe(viewLifecycleOwner){
            if (it != null) {
                Toast.makeText(context, "Data Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_editFragment_to_homeFragment)

            } else {
//                Log.d(TAG, "register failed: $it")
                Log.d("Create Seller", "_id : ${it}" )
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