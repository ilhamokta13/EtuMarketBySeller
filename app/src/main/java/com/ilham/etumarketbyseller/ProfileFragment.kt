package com.ilham.etumarketbyseller

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ilham.etumarketbyseller.databinding.FragmentProfileBinding
import com.ilham.etumarketbyseller.model.chat.User
import com.ilham.etumarketbyseller.model.profile.DataProfile
import com.ilham.etumarketbyseller.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    lateinit var binding : FragmentProfileBinding
    lateinit var pref : SharedPreferences
    lateinit var userVm : UserViewModel
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    private var filePath: Uri? = null

    private val PICK_IMAGE_REQUEST: Int = 2020

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)!!
        userVm = ViewModelProvider(this).get(UserViewModel::class.java)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)

        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference


//        databaseReference.addValueEventListener(object : ValueEventListener {
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val user = snapshot.getValue(User::class.java)
//                binding.etUserName.setText(user!!.fullname)
//
//                if (user!!.profileImage == "") {
//                    binding.uploadImage.setImageResource(R.drawable.profile)
//                } else {
//                    Glide.with(requireContext()).load(user.profileImage).into(binding.uploadImage)
//                }
//            }
//        })

//        binding.uploadImage.setOnClickListener {
//            chooseImage()
//        }



        binding.btnUpdate.setOnClickListener {
            updateprofile()
//            uploadImage()
        }

        getprofile()



        binding.btnLogout.setOnClickListener {
            val editor = pref.edit()
            editor.remove("token")
            editor.apply()
            pref.edit().clear().apply()
            Navigation.findNavController(binding.root).navigate(R.id.action_profileFragment_to_loginFragment)
        }








    }

    private fun chooseImage() {
        val intent: Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }




//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode != null) {
//            filePath = data!!.data
//            try {
//                var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), filePath)
//                binding.uploadImage.setImageBitmap(bitmap)
//                binding.btnSimpan.visibility = View.VISIBLE
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
//    }



//    private fun uploadImage() {
//        if (filePath != null) {
//
//            var ref: StorageReference = storageRef.child("image/" + UUID.randomUUID().toString())
//            ref.putFile(filePath!!)
//                .addOnSuccessListener {
//
//                    val hashMap:HashMap<String,String> = HashMap()
//                    hashMap.put("fullname",binding.etUserName.text.toString())
//                    hashMap.put("profileImage",filePath.toString())
//                    databaseReference.updateChildren(hashMap as Map<String, Any>)
//                    Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show()
//                    binding.btnSimpan.visibility = View.GONE
//                }
//                .addOnFailureListener {
//                    Toast.makeText(requireContext(), "Failed" + it.message, Toast.LENGTH_SHORT)
//                        .show()
//
//                }
//
//        }
//    }



    fun updateprofile(){
        val token = pref.getString("token", "").toString()
        val id = pref.getString("idbuyer","" ).toString()
        val pass= pref.getString("password", "").toString()
        val inputnama = binding.txtFullname.text.toString()
        val inputemail = binding.txtEmail.text.toString()
        val inputtelepon = binding.txtTelephone.text.toString()
        val inputrole = binding.txtRole.text.toString()
        val inputshopname = binding.txtShopname.text.toString()

        userVm.updateprofile(token,inputnama,inputemail,inputtelepon,inputrole, inputshopname)

//        val dataprofile = DataProfile(inputemail,inputnama,id,pass,inputrole,inputshopname,inputtelepon,0)

        userVm.responseupdateprofile.observe(viewLifecycleOwner){
            if (it != null) {
                Toast.makeText(context, "Update Profile Berhasil", Toast.LENGTH_SHORT).show()
            }
        }




    }

    fun getprofile(){
        val token = pref.getString("token", "").toString()

        userVm.getprofile(token)

        userVm.dataprofile.observe(viewLifecycleOwner){
            binding.txtFullname.setText(it.fullName)
            binding.txtTelephone.setText(it.telp)
            binding.txtEmail.setText(it.email)
            binding.txtShopname.setText(it.shopName)
            binding.txtRole.setText(it.role)
        }



    }








}