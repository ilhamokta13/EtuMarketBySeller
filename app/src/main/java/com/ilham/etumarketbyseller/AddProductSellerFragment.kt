package com.ilham.etumarketbyseller


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.RestrictionsManager.RESULT_ERROR
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.location.LocationServices

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
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

@AndroidEntryPoint
class AddProductSellerFragment : Fragment() {
    lateinit var binding: FragmentAddProductSellerBinding
    lateinit var pref: SharedPreferences
    private lateinit var productVm: ProductViewModel
    private var imageMultipart: MultipartBody.Part? = null
    private var imageUri: Uri? = Uri.EMPTY
    private var imageFile: File? = null
    private var titleAd:String? = null






    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAddProductSellerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)
        productVm = ViewModelProvider(this).get(ProductViewModel::class.java)



//        val dateNowRilis = productVm.getDate()
//        binding.etDate.setText(dateNowRilis)
        datePickerRilis()


        val hintTitle = resources.getStringArray(R.array.category)

        formTitle(hintTitle)







        binding.saveButton.setOnClickListener {
            postdata()

        }

        binding.uploadImage.setOnClickListener {
//           openGallery()
            getContent.launch("image/*")

        }

        binding.findlocation.setOnClickListener {
            getLocation()
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


    private fun namaMonth(): ArrayList<String> {
        val nameMonth = ArrayList<String>()
        nameMonth.add("Januari")
        nameMonth.add("Februari")
        nameMonth.add("Maret")
        nameMonth.add("April")
        nameMonth.add("Mei")
        nameMonth.add("Juni")
        nameMonth.add("Juli")
        nameMonth.add("Agustus")
        nameMonth.add("September")
        nameMonth.add("Oktober")
        nameMonth.add("November")
        nameMonth.add("Desember")
        return nameMonth
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun datePickerRilis() {


            binding.etDate.setOnClickListener {
                val nameMonth = namaMonth()
                val calendar = Calendar.getInstance()
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)


                val datePickerDialog = DatePickerDialog(
                    requireContext(),
                    { _, year, month, day ->
//                        val bulan = nameMonth[month]
//                        val tanggalRilis = "$year-${month+1}-$day"

                        val nameMonth = namaMonth()
                        val selectedCalendar = Calendar.getInstance()
                        selectedCalendar.set(year, month, day)

                        val timeZone = TimeZone.getTimeZone("UTC")
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        dateFormat.timeZone = timeZone
                        val formattedDate = dateFormat.format(selectedCalendar.time)

                        binding.etDate.setText(formattedDate)
                        productVm.saveDate(formattedDate)


                    },
                    year, month, day,
                )
                datePickerDialog.show()

                datePickerDialog.setOnDateSetListener { datePicker, _, _, _ ->
                    val month = datePicker.month
                    val tahunDeparture = datePicker.year
                    val hariDeparture = datePicker.dayOfMonth
                    val tanggalDeparture = "$tahunDeparture-${month + 1}-$hariDeparture"
                    productVm.saveDate(tanggalDeparture)
                    binding.etDate.setText(tanggalDeparture)
                }


                datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE)
                    .setTextColor(resources.getColor(R.color.DARKBLUE05))
                datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE)
                    .setTextColor(resources.getColor(R.color.DARKBLUE05))

            }


    }


    private fun formTitle(hintTitle: Array<String>) {
        binding.uploadCategory.apply {
            val adapterTitle = ArrayAdapter(requireContext(), R.layout.dropdown_item, hintTitle)
            setAdapter(adapterTitle)
            hint = "Title"
            onItemClickListener = AdapterView.OnItemClickListener{ _, _, position, _ ->
                titleAd = adapterTitle.getItem(position).toString()
            }
        }
    }

    private fun getLocation() {
        val fusedLocProvClient = LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 10
            )
        } else {
            fusedLocProvClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    binding.longitude.text = location.longitude.toString()
                    binding.latitude.text = location.latitude.toString()
                    binding.altitude.text = location.altitude.toString()
                    binding.edAcc.text = "${location.accuracy}%"

                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    try {
                        val addresses: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
                        if (addresses.isNotEmpty()) {
                            val locName: String = addresses[0].getAddressLine(0)
                            binding.uploadlocation.text = locName
                            productVm.saveLocation(locName)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(context, "Alamat Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }





    fun postdata() {


        val namaproduk = binding.uploadNamaProduk.text.toString().toRequestBody("text/plain".toMediaType())
        val harga = binding.uploadHargaMenu.text.toString().toRequestBody("text/plain".toMediaType())
        val deskripsi =
            binding.uploadDesc.text.toString().toRequestBody("text/plain".toMediaType())
        val categori = binding.uploadCategory.text.toString().toRequestBody("text/plain".toMediaType())
        val dateNowRilis = productVm.getDate().toString().toRequestBody("text/plain".toMediaType())
//        binding.etDate.setText(dateNowRilis)
        val release = binding.etDate.text.toString().toRequestBody("text/plain".toMediaType())
        val latitude = binding.latitude.text.toString().toRequestBody("text/plain".toMediaType())
        val longitude = binding.longitude.text.toString().toRequestBody("text/plain".toMediaType())
        val lokasi = binding.uploadlocation.text.toString()
        val price = binding.uploadHargaMenu.text
        val nameproduct = binding.uploadNamaProduk.text

        if (lokasi.isEmpty()) {
            Toast.makeText(context, "Lokasi belum diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if(price.isEmpty()){
            Toast.makeText(context, "Harga belum diisi", Toast.LENGTH_SHORT).show()
            return
        }

        if (nameproduct.isEmpty()){
            Toast.makeText(context, "Nama Produk belum diisi", Toast.LENGTH_SHORT).show()
            return
        }
//        val id = pref.getString("_id", "").toString().toRequestBody("text/plain".toMediaType())

//        val adduser = pref.edit()
//        adduser.putString("namaproduk",
//            namaproduk.toString().toRequestBody("text/plain".toMediaType()).toString()
//        )
//        adduser.putString("harga",
//            harga.toString().toRequestBody("text/plain".toMediaType()).toString()
//        )
//        adduser.putString("deskripsi",
//            deskripsi.toString().toRequestBody("text/plain".toMediaType()).toString()
//        )
//        adduser.putString("categori", categori.toString().toRequestBody("text/plain".toMediaType()).toString())
//        adduser.putString("release",
//            release.toString().toRequestBody("text/plain".toMediaType()).toString()
//        )
//        adduser.putString("location",
//            location.toString().toRequestBody("text/plain".toMediaType()).toString()
//        )
//        adduser.putString("image", imageMultipart.toString())
//        adduser.apply()



//        val id = "65bc6622cb2c0ebe09cdcc07"


//        val id = productVm.getTicketId()

        val token  =  pref.getString("token", "").toString()



        productVm.createproduct(
            namaproduk,
            harga,
            deskripsi,

            imageMultipart!!,

//            dataGambarUri.toString(),
            categori,
            token,
            dateNowRilis,
            latitude, longitude
        )



        productVm.responsecreate.observe(viewLifecycleOwner, androidx.lifecycle.Observer {

            if (it.message == "Product added") {
                Toast.makeText(context, "Data Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_addProductSellerFragment_to_homeFragment)
                val sharedPref = pref.edit()
                sharedPref.putString("id", it!!.data.id)
                sharedPref.putString("sellerID", it.data.sellerID.toString())
                sharedPref.apply()

            } else {
//                Log.d(TAG, "register failed: $it")

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