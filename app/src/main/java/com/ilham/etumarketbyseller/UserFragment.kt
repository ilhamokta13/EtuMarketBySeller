package com.ilham.etumarketbyseller

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.R
import com.google.firebase.messaging.FirebaseMessaging
import com.ilham.etumarketbyseller.constant.FirebaseService
import com.ilham.etumarketbyseller.databinding.FragmentUserBinding
import com.ilham.etumarketbyseller.model.chat.User
import com.ilham.etumarketbyseller.model.chat.UserAdapter
import org.json.JSONObject
import java.io.IOException


class UserFragment : Fragment() {

    lateinit var binding : FragmentUserBinding
    var userList = ArrayList<User>()
    lateinit var pref: SharedPreferences
    private var filePath: Uri? = null

    private val PICK_IMAGE_REQUEST: Int = 2020


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)
        FirebaseService.sharedPref = requireActivity().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)


        val token = pref.getString("token", "").toString()

        if (token.isEmpty() || isTokenExpired(token)) {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Login Required")
                .setMessage("Your session has expired. Please login again.")
                .setCancelable(false)
                .setPositiveButton("Login") { dialog, which ->
                    findNavController().navigate(com.ilham.etumarketbyseller.R.id.action_userFragment_to_loginFragment)
                }
                .setNegativeButton("Cancel") { dialog, which ->
                    // Tetap di halaman user
                }
                .show()
        } else {
            initializeFirebaseMessaging()
            setupRecyclerView()
            getUsersList()
        }







//        binding.imgBack.setOnClickListener {
//
//        }
//
//        binding.imgProfile.setOnClickListener {
//            findNavController().navigate(R.id.action_userFragment_to_profileFragment2)
//        }

        getUsersList()
    }


    private fun setupRecyclerView() {
        binding.userRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun initializeFirebaseMessaging() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null && !TextUtils.isEmpty(task.result)) {
                        val token: String = task.result!!
                        // Lakukan sesuatu dengan token jika perlu
                    }
                }
            }
    }

    fun getUsersList() {
        val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        var userid = firebase.uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userid")


        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Users")


        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                val currentUser = snapshot.getValue(User::class.java)
                if (currentUser!!.profileImage == ""){
                    binding.imgProfile.setImageResource(com.ilham.etumarketbyseller.R.drawable.profile)
                }else{
                    Glide.with(requireActivity()).load(currentUser.profileImage).into(binding.imgProfile)
                }

                for (dataSnapShot: DataSnapshot in snapshot.children) {
                    val user = dataSnapShot.getValue(User::class.java)

                    if (!user!!.userId.equals(firebase.uid)) {

                        userList.add(user)
                    }
                }


                binding.userRecyclerView.layoutManager = LinearLayoutManager(context,
                    LinearLayoutManager.VERTICAL,false)

                binding.userRecyclerView.adapter = UserAdapter(context!!,userList)
            }

        })
    }


    fun isTokenExpired(token: String): Boolean {
        try {
            val split = token.split(".")
            val decodedBytes = Base64.decode(split[1], Base64.URL_SAFE)
            val decodedString = String(decodedBytes, Charsets.UTF_8)
            val jsonObject = JSONObject(decodedString)
            val exp = jsonObject.getLong("exp")
            val currentTime = System.currentTimeMillis() / 1000
            return currentTime > exp
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true // Jika terjadi kesalahan, anggap token sudah kedaluwarsa
    }



}