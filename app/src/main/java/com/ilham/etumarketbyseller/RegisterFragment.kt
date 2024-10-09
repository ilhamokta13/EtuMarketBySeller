package com.ilham.etumarketbyseller

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.ilham.etumarketbyseller.databinding.FragmentRegisterBinding
import com.ilham.etumarketbyseller.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    lateinit var binding: FragmentRegisterBinding
    lateinit var firebaseAuth: FirebaseAuth
    private lateinit var userVm: UserViewModel
    lateinit var pref: SharedPreferences
    private lateinit var databaseReference: DatabaseReference
    private var titleAd:String? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)
        userVm = ViewModelProvider(this).get(UserViewModel::class.java)

        binding.btnRegister.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val pass = binding.passEditText.text.toString()
            val role = binding.etRole.text.toString()
            val telepon = binding.teleponEditText.text.toString()
            val shopName = binding.shopNameEditText.text.toString()


            if (username.isEmpty() || email.isEmpty() || pass.isEmpty() || telepon.isEmpty() || role.isEmpty() || shopName.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all the field", Toast.LENGTH_SHORT)
                    .show()
            } else {
                firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user: FirebaseUser? = firebaseAuth.currentUser
                        val userId: String = user!!.uid
                        databaseReference =
                            FirebaseDatabase.getInstance().getReference("Users").child(userId)
                        Log.d("Uid", "print:$userId")
                        checkIfUsernameExists(userId, username, email, pass, telepon, role, shopName)
                    } else {
                        Toast.makeText(requireContext(), "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        binding.tvMasukdisini.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }


        val hintTitle = resources.getStringArray(R.array.Role)

        formTitle(hintTitle)

    }


//    fun register() {
//
//    }


    private fun register(name: String, user: String, pass: String){
        firebaseAuth.createUserWithEmailAndPassword(user, pass).addOnCompleteListener {
            if (it.isSuccessful) {
                val user: FirebaseUser? = firebaseAuth.currentUser
                val userId: String = user!!.uid

                databaseReference =
                    FirebaseDatabase.getInstance().getReference("Users").child(userId)

                val hashMap: HashMap<String, String> = HashMap()
                hashMap.put("userId", userId)
                hashMap.put("fullname", name)
                hashMap.put("profileImage", "")

                databaseReference.setValue(hashMap).addOnCompleteListener {
                    if (it.isSuccessful) {
                        //open home activity
                        binding.usernameEditText.setText(" ")
                        binding.emailEditText.setText("")
                        binding.passEditText.setText("")

//                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)

                    }
                }
            }
        }


    }

    private fun checkIfUsernameExists(userId: String, username: String, email: String, pass: String, telepon: String, role: String, shopName: String) {
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.orderByChild("fullname").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(
                            requireContext(),
                            "Username already exists. Please choose another one.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        userVm.postregist(userId, username, email, pass, telepon, role, shopName)
                        userVm.responseregister.observe(viewLifecycleOwner) {
                            if (it.message == "Register success") {
                                Toast.makeText(
                                    requireContext(),
                                    "${it.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val sharedPref = pref.edit()
                                sharedPref.putString("email", email)
                                sharedPref.putString("telephone", telepon)
                                sharedPref.putString("fullname", username)
                                sharedPref.putString("password", pass)
                                sharedPref.apply()
                                findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                                Toast.makeText(context, "Berhasil Registrasi", Toast.LENGTH_SHORT)
                                    .show()
                            } else {
                                Toast.makeText(context, "Regis tidak berhasil", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun formTitle(hintTitle: Array<String>) {
        binding.etRole.apply {
            val adapterTitle = ArrayAdapter(requireContext(), R.layout.dropdown_item, hintTitle)
            setAdapter(adapterTitle)
            hint = "Title"
            onItemClickListener = AdapterView.OnItemClickListener{ _, _, position, _ ->
                titleAd = adapterTitle.getItem(position).toString()
            }
        }
    }



}