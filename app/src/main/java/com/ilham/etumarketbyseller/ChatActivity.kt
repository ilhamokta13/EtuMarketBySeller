package com.ilham.etumarketbyseller

import android.Manifest
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.ilham.etumarketbyseller.databinding.ActivityChatBinding
import com.ilham.etumarketbyseller.model.chat.*
import com.ilham.etumarketbyseller.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@AndroidEntryPoint
class ChatActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var binding : ActivityChatBinding
    private lateinit var pref: SharedPreferences
    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    private var selectedImagePath: String? = null
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null
    lateinit var gMap: GoogleMap
    var chatList = ArrayList<Chat>()
    var topic = ""

    private lateinit var locationManager: LocationManager
    lateinit var productVm : ProductViewModel

    companion object {
        const val CHANNEL_ID = "my_channel_id"
        const val NOTIFICATION_ID = 1
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = this.getSharedPreferences("Success", Context.MODE_PRIVATE)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        productVm = ViewModelProvider(this).get(ProductViewModel::class.java)

        binding.chatRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        FirebaseApp.initializeApp(this)


        var intent = getIntent()
        var userId = intent.getStringExtra("userId")
        var userName = intent.getStringExtra("fullname")


        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_api_key))
        }





        binding.tvUserName.text = userName

        Glide.with(applicationContext)
            .load(R.drawable.profile_image)
            .into(binding.imgProfile)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)

        binding.btnSendImage.setOnClickListener {
            openImagePicker()
        }

        binding.btnSendLocation.setOnClickListener {
            getLocation()
        }



        binding.btnSendMessage.setOnClickListener {
            sendMessageWithOptionalLocationAndImage()
        }


        readMessage(firebaseUser!!.uid, userId)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImagePath = data.data?.toString()
            // Tampilkan gambar sebelum dikirim
            showImagePreview(selectedImagePath)
        }
    }

    private var selectedImageUri: Uri? = null

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_REQUEST)
    }

    // Add this constant
    private val IMAGE_PICK_REQUEST = 101






    private fun uploadImage(message: String) {
        val userId = intent.getStringExtra("userId")
        val storageRef = FirebaseStorage.getInstance().reference.child("chat_images/${System.currentTimeMillis()}")
        val imageUri = Uri.parse(selectedImagePath)

        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    val imageUrl = uri.toString()
                    // Kirim pesan dengan URL gambar (dan teks jika ada)
                    sendMessage(firebaseUser!!.uid, userId!!, message, imageUrl)
                }
            }
            .addOnFailureListener { e ->
                Log.e("TAG", "Error uploading image: ${e.message}")
            }
    }

    private fun sendMessage(senderId: String, receiverId: String, message: String, imageUrl: String? = null) {
        var reference: DatabaseReference? = FirebaseDatabase.getInstance().getReference()

        var hashMap: HashMap<String, String> = HashMap()
        hashMap.put("senderId", senderId)
        hashMap.put("receiverId", receiverId)
        hashMap.put("message", message)
//
//        if (!imageUrl.isNullOrEmpty()) {
//            hashMap["imageUrl"] = imageUrl
//        }

        val chat = Chat(senderId, receiverId, message, imageUrl)


        reference!!.child("Chat").push().setValue(chat)

    }


    private fun getLocation() {
        val fusedLocProvClient = LocationServices.getFusedLocationProviderClient(this)
        val boundsBuilder = LatLngBounds.Builder()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), 10
                )
            }
        } else {
            fusedLocProvClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude


                    displayLocationOnMap(currentLatitude!!, currentLongitude!!)
                    val geocoder = Geocoder(this, Locale.getDefault())
                    try {
                        val addresses: List<Address> = geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
                        if (addresses.isNotEmpty()) {
                            val locName: String = addresses[0].getAddressLine(0)

                            productVm.saveLocation(locName)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    // Panggil showMap dengan koordinat lokasi pengguna
//                    showMap(location.latitude, location.longitude)

                    val latLon = LatLng(location.latitude, location.longitude)
                    Log.d("Detail Rs", "$latLon")

                    gMap.addMarker(
                        MarkerOptions()
                            .position(latLon)
                            .icon(vectorToBitmap(R.drawable.ic_baseline_location_on_24, Color.RED))
                    )

                    boundsBuilder.include(latLon)
                    val bounds: LatLngBounds = boundsBuilder.build()
                    gMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 64))
                } else {
                    Toast.makeText(this, "Alamat Berhasil Ditambahkan", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { e ->
                Toast.makeText(this, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }



    fun readMessage(senderId: String, receiverId: String) {
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Chat")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val chat = dataSnapShot.getValue(Chat::class.java)

                    if (chat!!.senderId.equals(senderId) && chat!!.receiverId.equals(receiverId) ||
                        chat!!.senderId.equals(receiverId) && chat!!.receiverId.equals(senderId)
                    ) {
                        chatList.add(chat)
                    }
                }

                val chatAdapter = ChatAdapter(this@ChatActivity, chatList)

                binding.chatRecyclerView.adapter = chatAdapter
            }
        })
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            withContext(Dispatchers.IO) {

                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
//                    Log.d("TAG", "Response: ${Gson().toJson(response)}")
                } else {
                    Log.e("TAG", response.errorBody()!!.string())
                }

                // Jika notifikasi berisi gambar
                if (!notification.data.imageUrl.isNullOrEmpty()) {
                    // Ambil gambar dari URL
                    val bitmap = getBitmapFromUrl(notification.data.imageUrl!!)

                    // Jika gambar berhasil diambil
                    if (bitmap != null) {
                        // Tampilkan notifikasi dengan gambar menggunakan BigPictureStyle
                        showNotificationWithImage(notification, bitmap)
                    }
                }
            }
        } catch(e: Exception) {
            Log.e("TAG", e.toString())
        }
    }




    // Fungsi untuk mendapatkan bitmap dari URL
    fun getBitmapFromUrl(imageUrl: String): Bitmap? {
        return try {
            val url = URL(imageUrl)
            BitmapFactory.decodeStream(url.openConnection().getInputStream())
        } catch (e: IOException) {
            Log.e("TAG", "Error fetching image from URL: ${e.message}")
            null
        }
    }



    // Fungsi untuk menampilkan notifikasi dengan gambar
    fun showNotificationWithImage(notification: PushNotification, bitmap: Bitmap) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Konfigurasi notifikasi
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(notification.data.title)
            .setContentText(notification.data.message)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        // Tambahkan BigPictureStyle dengan gambar
        val style = NotificationCompat.BigPictureStyle()
            .bigPicture(bitmap)
            .bigLargeIcon(null as Bitmap?)
            .setBigContentTitle(notification.data.title)
            .setSummaryText(notification.data.message)

        builder.setStyle(style)

        // Tampilkan notifikasi
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun showImagePreview(imagePath: String?) {
        // Periksa apakah path gambar tidak null atau kosong
        if (!imagePath.isNullOrEmpty()) {
            // Tampilkan tata letak gambar sebelum dikirim
            binding.layoutImagePreview.visibility = View.VISIBLE

            // Gunakan Glide atau Picasso untuk menampilkan gambar di ImageView
            // Contoh menggunakan Glide:
            Glide.with(this)
                .load(imagePath)
                .into(binding.imgPreview)

            // Tambahkan listener untuk tombol hapus gambar
            binding.btnRemoveImage.setOnClickListener {
                // Hapus path gambar yang dipilih
                selectedImagePath = null
                // Sembunyikan tata letak gambar sebelum dikirim
                binding.layoutImagePreview.visibility = View.GONE
            }
        }
    }

    private fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }


    private fun sendMessageWithOptionalLocationAndImage() {
        val userId = intent.getStringExtra("userId")
        val userName = intent.getStringExtra("fullname")
        val message: String = binding.etMessage.text.toString().trim()

        Log.d("ChatActivity", "sendMessageWithOptionalLocationAndImage called")
        Log.d("ChatActivity", "currentLatitude: $currentLatitude, currentLongitude: $currentLongitude, selectedImagePath: $selectedImagePath")

        // Jika lokasi tersedia, kirim pesan lokasi
        if (currentLatitude != null && currentLongitude != null) {
            val mapsUrl = "https://www.google.com/maps/search/?api=1&query=$currentLatitude,$currentLongitude"
            sendMessage(firebaseUser!!.uid, userId!!, mapsUrl)
            binding.etMessage.setText("")
            currentLatitude = null
            currentLongitude = null
            Log.d("ChatActivity", "Location message sent: $mapsUrl")
        }
        // Jika ada gambar yang dipilih, upload gambar dan kirim pesan
        else if (!selectedImagePath.isNullOrEmpty()) {
            uploadImage(message)
            Log.d("ChatActivity", "Image upload initiated")
        }
        // Jika hanya pesan teks yang tersedia, kirim pesan teks
        else if (message.isNotEmpty()) {
            sendMessage(firebaseUser!!.uid, userId!!, message)
            binding.etMessage.setText("")
            Log.d("ChatActivity", "Text message sent: $message")
        }

        selectedImagePath = null
        topic = "/topics/$userId"

        // Jika ada pesan yang dikirim atau gambar yang dipilih, kirim notifikasi
        if (!message.isNullOrEmpty() || !selectedImagePath.isNullOrEmpty()) {
            PushNotification(
                NotificationData(userName!!, message),
                topic
            ).also {
                sendNotification(it)
                Log.d("ChatActivity", "Notification sent")
            }
        }

        binding.layoutImagePreview.visibility = View.GONE
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this,
                R.raw.map_style
            )
        )
        gMap.uiSettings.isZoomControlsEnabled = true
        gMap.uiSettings.isIndoorLevelPickerEnabled = true
        gMap.uiSettings.isCompassEnabled = true
        gMap.uiSettings.isMapToolbarEnabled = true

        if (currentLatitude != null && currentLongitude != null) {
            displayLocationOnMap(currentLatitude!!, currentLongitude!!)
        }
    }

    private fun displayLocationOnMap(latitude: Double, longitude: Double) {
        val location = LatLng(latitude, longitude)
        gMap.addMarker(
            MarkerOptions()
                .position(location)
                .icon(vectorToBitmap(R.drawable.ic_baseline_location_on_24, Color.RED))
                .title("Current Location")
        )
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }


}

