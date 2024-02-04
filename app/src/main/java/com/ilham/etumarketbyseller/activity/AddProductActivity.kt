package com.ilham.etumarketbyseller.activity


import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.ilham.etumarketbyseller.MainActivity
import com.ilham.etumarketbyseller.R
import com.ilham.etumarketbyseller.databinding.ActivityAddProductBinding
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.security.Permission

class AddProductActivity : AppCompatActivity() {
    lateinit var binding : ActivityAddProductBinding
    private lateinit var currentPhotoPath : String
    var foto = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //mengaktifkan tombol id UploadImage (ImageView) untuk dapat di klik
        binding.uploadImage.isEnabled = true
        //pada id uploadImage (ImageView) menampilkan gambar dengan nama file uploadimng (pada drawable)
        binding.uploadImage.setImageResource(R.drawable.uploadimg)

        //melakukan pemeriksaan izin untuk kamera apakah telah diaktifkan (diberikan)
        if (ActivityCompat.checkSelfPermission(
                this,
                    Manifest.permission.CAMERA
                //jika izin kamera belum diberikan
            )!= PackageManager.PERMISSION_GRANTED
        ){
            //maka meminta izin kamera dengan menggunakan "requestPermissions"
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 100)
        }else{
            //jika izin telah diberikan, maka tidak perlu meminta izin.  dan tombol UploadImage dapat di klik untuk upload gambar
            binding.uploadImage.isEnabled = true
        }

        //jika id uploadImage di klik, menjalankan fungsi selectImage
        binding.uploadImage.setOnClickListener {
            selectImage()
        }
        //jika id saveButton di klik, menjalankan fungsi uploadData
        binding.saveButton.setOnClickListener {
            uploadData()
        }


    }


    private fun selectImage(){
        //membuat array "items" dengan 3 pilihan
        val items = arrayOf<CharSequence>("Take Photo", "Choose from Library", "Cancel")
        //membuat variabel "builder"
        val builder = android.app.AlertDialog.Builder(this)
        //dengan judul "EaTrain Admin"
        builder.setTitle(getString(R.string.app_name))
        //dan ikon aplikasi
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setItems(items) { dialog: DialogInterface, item: Int ->
            //jika memilih opsi "Take Photo"
            if (items[item] == "Take Photo") {
                // Ambil gambar menggunakan kamera
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                    // Pastikan ada aplikasi kamera yang dapat menangani intent ini
                    takePictureIntent.resolveActivity(packageManager)?.also {
                        // Buat file gambar sementara untuk menyimpan hasil kamera
                        val photoFile: File? = try {
                            //membuat file gambar dengan fungsi createImageFile untuk penyimpanan gambar yang di capture
                            createImageFile()
                        } catch (ex: IOException) {
                            // Error saat membuat file
                            Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT)
                                .show()
                            null
                        }
                        // Jika file berhasil dibuat, lanjutkan mengambil gambar dari kamera
                        photoFile?.also {
                            //memperoleh uri file yang akan digunakan untuk menyimpan hasil foto
                            val photoURI: Uri = FileProvider.getUriForFile(
                                this, "com.ilham.etumarketbyseller.fileprovider", it
                            )
                            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                            startActivityForResult(takePictureIntent, 10)
                        }
                    }
                }
            }
            //mengambil gambar dari galeri
            else if (items[item] == "Choose from Library") {
                //membuat variabel intent untuk memilih gambar dari galeri
                val intent = Intent(Intent.ACTION_PICK)
                //mengatur tipe intent, untuk membatasi bahwa yang dipilih hanya pada tipe gambar
                intent.type = "image/*"
                //memulai aktivitas selectImage dengan intent dan kode permintaan 20
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 20)
                //jika opsi yang dipilih cancel
            } else if (items[item] == "Cancel") {
                //dialog akan ditutup
                dialog.dismiss()
            }
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //galeri
        if (requestCode == 20 && resultCode == RESULT_OK && data != null) {
            val path : Uri? = data.data
            //crop
            //Membuat URI tujuan untuk menyimpan gambar hasil cropping
            val destinationUri = Uri.fromFile(File(cacheDir, "IMG_" + System.currentTimeMillis()))
            // Mengatur opsi-opsi untuk fitur cropping

        }
        //kamera
        if (requestCode == 10 && resultCode == RESULT_OK) {
            val imageUri = Uri.fromFile(File(currentPhotoPath))
            //crop
            //Membuat URI tujuan untuk menyimpan gambar hasil cropping
            val path = Uri.fromFile(File(cacheDir, "IMG_" + System.currentTimeMillis()))
            // Mengatur opsi-opsi untuk fitur cropping

        }


        //menangkap hasil cropping dan update imageview

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Membuat nama file
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Simpan path file di variabel global
            currentPhotoPath = absolutePath
        }
    }

    private fun uploadData(){
        val menu = binding.uploadJudulMenu.text.toString()
        val harga = binding.uploadHargaMenu.text.toString()
        val desc = binding.uploadDesc.text.toString()
        // jika data yang dibutuhkan telah diisi, lanjut ke proses upload
        if (menu.isNotEmpty() && harga.isNotEmpty() && desc.isNotEmpty() && foto != "") {
            binding.uploadImage.isDrawingCacheEnabled = true
            binding.uploadImage.buildDrawingCache()
            val bitmap = (binding.uploadImage.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            //UPLOAD
            //membuat progress dialoag (ikon loading)
            val builder = AlertDialog.Builder(this)
            builder.setCancelable(false)
            builder.setView(R.layout.progress_layout)
            val dialog = builder.create()
            dialog.show()

            //mendapatkan instance FirebaseStorage
            val storage = FirebaseStorage.getInstance()
            //inisialisasi untuk menyimpan gambar ke folder "images" dengan nama file yg telah ditentukan
            val reference = storage.getReference("images").child("IMG"+ Date().time +".jpeg")
            //mengunggah gambar ke firebaseStorage
            var uploadTask = reference.putBytes(data)
            //jika gagal saat mengunggah gambar ke firebaseStorage
            uploadTask.addOnFailureListener {
                //menampilkan toast failed
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                //jika sukses saat mengunggah gambar ke firebaseStorage
            }.addOnSuccessListener { taskSnapshot ->
                //note : metadata = informasi tambahan yang terkait dengan file yang diunggah, seperti nama file, tipe file,dll
                //memeriksa apakah metadata tidak null
                if(taskSnapshot.metadata !=null){
                    //jika tidak null
                    if(taskSnapshot.metadata!!.reference !=null){
                        //mengambil URL unduhan gambar
                        taskSnapshot.metadata!!.reference!!.downloadUrl.addOnCompleteListener {
                            //URL undahan gambar diambil dan disimpan pada variabel foto
                            var foto = it.getResult().toString()
                            //memanggil fungsi saveData menggunakan data menu, harga, deskripsi, dan URL unduhan gambar
                            saveData(menu, harga, desc, foto)
                        }
                        //jika taskSnapshot.metadata!!.reference null, tidak ada referensi yang diperoleh
                    }else{
                        //dialog progress ditutup dan menampilkan toast failed
                        dialog.dismiss()
                        Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                    }
                    //Jika taskSnapshot.metadata null, maka tidak ada metadata yang diperoleh dari proses unggah gambar.
                }else{
                    ////dialog progress ditutup dan menampilkan toast failed
                    dialog.dismiss()
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            // jika data yang dibutuhkan belum diisi, tampilkan pesan error
            Toast.makeText(this, "Data tidak lengkap", Toast.LENGTH_SHORT).show()
        }
    }

    //fungsi untuk menyimpan data menu ke FirebaseFirestore
    private fun saveData(menu: String, harga : String, desc : String,  foto : String,){
        //mendapatkan instance Firestore
        val db = FirebaseFirestore.getInstance()
        //membuat hashMap yang berisi data menu (listMenu) yang akan disimpan
        val listMenu = hashMapOf<String, Any>(
            "namaMenu" to menu,
            "Harga" to harga,
            "Foto" to foto,
            "Desc" to desc,
        )
        //menambahkan data menu (listMenu) tersebut ke koleksi "Menu"
        db.collection("Menu")
            .add(listMenu)
            //jika data berhasil disimpan
            .addOnSuccessListener { documentReference ->
                //menampilkan toast succes
                Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
                //berpindah halaman ke MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            //jika data gagal disimpan
            .addOnFailureListener { exception ->
                //menampilkan toast failed
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
            }

    }









}