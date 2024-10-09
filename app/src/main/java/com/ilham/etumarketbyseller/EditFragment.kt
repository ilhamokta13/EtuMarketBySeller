package com.ilham.etumarketbyseller

import android.Manifest
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import com.ilham.etumarketbyseller.databinding.FragmentEditBinding
import com.ilham.etumarketbyseller.viewmodel.HomeViewModel
import com.ilham.etumarketbyseller.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class EditFragment : Fragment() {
    lateinit var binding : FragmentEditBinding
    lateinit var pref: SharedPreferences
    private lateinit var productVm: ProductViewModel
    private var imageMultipart: MultipartBody.Part? = null
    private var imageUri: Uri? = Uri.EMPTY
    private lateinit var dataGambarUri: Uri
    private var imageFile: File? = null
    private lateinit var idEdit: String
    private lateinit var idProduct: String
    private lateinit var homeVm : HomeViewModel
    private var titleAd:String? = null


    companion object {
        const val IMAGE_REQUEST_CODE = 1_000
    }




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)
        productVm = ViewModelProvider(this).get(ProductViewModel::class.java)
        homeVm = ViewModelProvider(this).get(HomeViewModel::class.java)








        val hintTitle = resources.getStringArray(R.array.category)

        formTitle(hintTitle)

        datePickerRilis()




        getdataproduct()

        binding.uploadImage.setOnClickListener {
            getContent.launch("image/*")

        }






        binding.saveButton.setOnClickListener {
            if (imageMultipart != null) {
                updatedata()
            } else {
                // Tampilkan pesan kesalahan atau lakukan tindakan lain sesuai kebutuhan
                Toast.makeText(context, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
            }




        }

        binding.findlocation.setOnClickListener {
            getLocation()
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
                        val MIN_LATITUDE = -90.0
                        val MAX_LATITUDE = 90.0
                        val MIN_LONGITUDE = -180.0
                        val MAX_LONGITUDE = 180.0

                        if (location.latitude !in MIN_LATITUDE..MAX_LATITUDE) {
                            throw IllegalArgumentException("Latitude is out of range: ${location.latitude}")
                        }
                        if (location.longitude !in MIN_LONGITUDE..MAX_LONGITUDE) {
                            throw IllegalArgumentException("Longitude is out of range: ${location.longitude}")
                        }

                        val addresses: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
                        if (addresses.isNotEmpty()) {
                            val locName: String = addresses[0].getAddressLine(0)
                            binding.uploadlocation.text = locName
                            productVm.saveLocation(locName)
                        }
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                        binding.uploadlocation.text = "Invalid latitude or longitude."
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
                    { _, year, month, dayOfMonth ->
                        val nameMonth = namaMonth()
                        val selectedCalendar = Calendar.getInstance()
                        selectedCalendar.set(year, month, dayOfMonth)

                        val timeZone = TimeZone.getTimeZone("UTC")
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        dateFormat.timeZone = timeZone
                        val formattedDate = dateFormat.format(selectedCalendar.time)
                        binding.etDate.setText(formattedDate)
                        productVm.saveEditDate(formattedDate)
                    },
                    year, month, day,
                )
                datePickerDialog.show()

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




    fun updatedata(){
        val namaproduk = binding.uploadNamaProduk.text.toString().toRequestBody("text/plain".toMediaType())
        val harga = binding.uploadHargaMenu.text.toString().toRequestBody("text/plain".toMediaType())
        val deskripsi =
            binding.uploadDesc.text.toString().toRequestBody("text/plain".toMediaType())
        val categori = binding.uploadCategory.text.toString().toRequestBody("text/plain".toMediaType())
        val dateNowRilis = productVm.geteditdate()
        binding.etDate.setText(dateNowRilis)
        val tglrelease = productVm.geteditdate().toString().toRequestBody("text/plain".toMediaType())
        val latitude = binding.latitude.text.toString().toRequestBody("text/plain".toMediaType())
        val lonitude = binding.longitude.text.toString().toRequestBody("text/plain".toMediaType())
        val stock = binding.uploadstok.text.toString().toRequestBody("text/plain". toMediaType())
        val token  =  pref.getString("token", "").toString()
        val getDataProduct = arguments?.getString("idcart")






        productVm.updateproduct(token,getDataProduct!!,namaproduk,harga,deskripsi,imageMultipart!!,categori,tglrelease,lonitude,latitude, stock)


        productVm.responseupdate.observe(viewLifecycleOwner){
            if (it != null) {
                findNavController().navigate(R.id.action_editFragment_to_homeFragment)
                Toast.makeText(context, "Data Berhasil Diupdate", Toast.LENGTH_SHORT).show()
            } else {
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

     fun getdataproduct(){
         val getDataProduct = arguments?.getString("idcart")
         val token  =  pref.getString("token", "").toString()
        homeVm.getproductperid(token, getDataProduct!!)
         val imageUserProfile = binding.uploadImage
         homeVm.dataproductperid.observe(viewLifecycleOwner){
             if (it != null) {
                 binding.uploadNamaProduk.setText(it.nameProduct)
                 binding.uploadHargaMenu.setText(it.price.toString())
                 binding.uploadCategory.setText(it.category)
                 binding.etDate.setText(it.releaseDate)
                 binding.uploadDesc.setText(it.description)

                 val lon = it.longitude
                 val lat = it.latitude

                 val geocoder = Geocoder(requireContext(), Locale.getDefault())
                 try {
                     val addresses: List<Address> = geocoder.getFromLocation(lat, lon, 1) as List<Address>
                     if (addresses.isNotEmpty()) {
                         val locName: String = addresses[0].getAddressLine(0)
                         binding.uploadlocation.text = locName
                     }
                 } catch (e: IOException) {
                     e.printStackTrace()
                 }
             } else {
                 Toast.makeText(context, "Alamat Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
             }


                 context?.let { it1 ->
                     Glide.with(it1)
                         .load("https://7895jr9m-3000.asse.devtunnels.ms/uploads/${it.image}")
                         .into(imageUserProfile)
                 }




             }

         }


    }








