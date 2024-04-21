package com.ilham.etumarketbyseller

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.ilham.etumarketbyseller.databinding.ActivityChatBinding
import com.ilham.etumarketbyseller.model.chat.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

class ChatActivity : AppCompatActivity() {
    lateinit var binding : ActivityChatBinding
    private lateinit var pref: SharedPreferences
    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    private var selectedImagePath: String? = null
    var chatList = ArrayList<Chat>()
    var topic = ""

    companion object {
        const val CHANNEL_ID = "my_channel_id"
        const val NOTIFICATION_ID = 1
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = this.getSharedPreferences("Success", Context.MODE_PRIVATE)

        binding.chatRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        FirebaseApp.initializeApp(this)


        var intent = getIntent()
        var userId = intent.getStringExtra("userId")
        var userName = intent.getStringExtra("fullname")


        binding.tvUserName.text = userName

        Glide.with(applicationContext)
            .load(R.drawable.profile_image)
            .into(binding.imgProfile)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)

        binding.btnSendImage.setOnClickListener {
            openImagePicker()
        }


        binding.btnSendMessage.setOnClickListener {
            // Extracted the user ID and user name from the intent
            val userId = intent.getStringExtra("userId")
            val userName = intent.getStringExtra("fullname")

            // Get the message from the EditText
            val message: String = binding.etMessage.text.toString().trim()

            // Check if an image is selected
            if (!selectedImagePath.isNullOrEmpty()) {
                // Image selected, upload to Firebase Storage
                uploadImage(message)
            } else if (message.isNotEmpty()) {
                // Only send the text message if there is text and no image is selected
                sendMessage(firebaseUser!!.uid, userId!!, message)
                binding.etMessage.setText("")  // Clear the EditText
            }

            // Reset selectedImagePath
            selectedImagePath = null

            topic = "/topics/$userId"

            // If there's a message or an image URL, send a notification
            if (!message.isNullOrEmpty() || !selectedImagePath.isNullOrEmpty()) {
                PushNotification(
                    NotificationData(userName!!, message),
                    topic
                ).also {
                    sendNotification(it)
                }
            }

            // Sembunyikan tata letak gambar sebelum dikirim
            binding.layoutImagePreview.visibility = View.GONE
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












}

