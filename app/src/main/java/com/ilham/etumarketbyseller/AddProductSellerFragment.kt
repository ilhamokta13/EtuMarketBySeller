package com.ilham.etumarketbyseller


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.RestrictionsManager.RESULT_ERROR
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference



import com.ilham.etumarketbyseller.databinding.FragmentAddProductSellerBinding
import com.ilham.etumarketbyseller.model.product.create.Data
import com.ilham.etumarketbyseller.viewmodel.ProductViewModel
import com.ilham.etumarketbyseller.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

@AndroidEntryPoint
class AddProductSellerFragment : Fragment() {
    lateinit var binding: FragmentAddProductSellerBinding
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
        binding = FragmentAddProductSellerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)
        productVm = ViewModelProvider(this).get(ProductViewModel::class.java)



        binding.saveButton.setOnClickListener {
            postdata()

        }

        binding.uploadImage.setOnClickListener {
//           openGallery()
            getContent.launch("image/*")

        }


    }


//    private fun openGallery() {
//        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        startActivityForResult(galleryIntent, IMAGE_REQUEST_CODE)
//        filePhoto = getPhotoFile(FILE_NAME)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
//            val imageUri = data?.data
//
//
//            // Update dataGambarUri
//            dataGambarUri = imageUri!!
//
//            // memuat gambar yang dipilih ke dalam ImageView dan menerapkan efek CircleCrop agar gambar berbentuk lingkaran
//            loadImageWithCircleCrop(dataGambarUri)
//        }
//    }
//
//    private fun getPhotoFile(fileName: String): File {
//        val directoryStorage = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//        return File.createTempFile(fileName, ".jpg", directoryStorage)
//    }
//
//    private fun loadImageWithCircleCrop(imageUri: Uri) {
//        Glide.with(this)
//            .load(imageUri)
//            .apply(RequestOptions.bitmapTransform(CircleCrop()))
//            .into(binding.uploadImage)
//    }


    fun postdata() {
        val namaproduk = binding.uploadNamaProduk.text.toString().toRequestBody("text/plain".toMediaType())
        val harga = binding.uploadHargaMenu.text.toString().toRequestBody("text/plain".toMediaType())
        val deskripsi =
            binding.uploadDesc.text.toString().toRequestBody("text/plain".toMediaType())
        val categori = binding.uploadCategory.text.toString().toRequestBody("text/plain".toMediaType())
        val release = binding.uploadreleaseDate.text.toString().toRequestBody("text/plain".toMediaType())
        val location = binding.uploadlocation.text.toString().toRequestBody("text/plain".toMediaType())
//        val id = pref.getString("_id", "").toString().toRequestBody("text/plain".toMediaType())

        val adduser = pref.edit()
        adduser.putString("namaproduk", namaproduk.toString())
        adduser.putString("harga", harga.toString())
        adduser.putString("deskripsi", deskripsi.toString())
        adduser.putString("categori", categori.toString())
        adduser.putString("release", release.toString())
        adduser.putString("location", location.toString())
        adduser.putString("image", imageMultipart.toString())
        adduser.apply()



//        val id = "65bc6622cb2c0ebe09cdcc07"

        val token  =  pref.getString("token", "").toString()



        productVm.createproduct(
            namaproduk,
            harga,
            deskripsi,

            imageMultipart!!,

//            dataGambarUri.toString(),
            categori,
            token,
            release,
            location
        )

        productVm.responsecreate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                Toast.makeText(context, "Data Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_addProductSellerFragment_to_homeFragment)

            } else {
//                Log.d(TAG, "register failed: $it")
                Log.d("Create Seller", "_id : ${it}" )
            }
        })


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