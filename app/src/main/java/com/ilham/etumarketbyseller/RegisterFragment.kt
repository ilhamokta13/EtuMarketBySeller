package com.ilham.etumarketbyseller

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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
            val role = binding.confirmpassEditText.text.toString()
            val telepon = binding.teleponEditText.text.toString()


            if (username.isEmpty() || email.isEmpty() || pass.isEmpty()  || telepon.isEmpty() || role.isEmpty())  {
                Toast.makeText(requireContext(), "Please fill all the field", Toast.LENGTH_SHORT).show()

            } else {
                userVm.postregist(username, email, pass, telepon, role,)
                register(username,email,pass)
                userVm.responseregister.observe(viewLifecycleOwner){
                    if (it.message == "Register success") {
                        Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()

                        val sharedPref = pref.edit()
                        sharedPref.putString("email", email)
                        sharedPref.putString("telephone", telepon)
                        sharedPref.putString("fullname", username)
                        sharedPref.putString("password", pass)
//                    sharedPref.putString("_id", id)
                        sharedPref.apply()

                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        Toast.makeText(context, "Berhasil Registrasi", Toast.LENGTH_SHORT)
                            .show()
                    }else{
                        Toast.makeText(context, "Regis tidak berhasil", Toast.LENGTH_SHORT).show()

                    }

                }

            }
        }

//        binding.tvMasukdisini.setOnClickListener {
//            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
//        }

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


}