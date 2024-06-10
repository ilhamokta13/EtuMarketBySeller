package com.ilham.etumarketbyseller

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.ilham.etumarketbyseller.databinding.FragmentResetPassBinding
import com.ilham.etumarketbyseller.model.product.status.PostForgotPass
import com.ilham.etumarketbyseller.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPassFragment : Fragment() {

    lateinit var binding : FragmentResetPassBinding
    lateinit var pref : SharedPreferences
    lateinit var userVm : UserViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       binding = FragmentResetPassBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref =requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)
        userVm = ViewModelProvider(this).get(UserViewModel::class.java)

        binding.btnReset.setOnClickListener {
            val email = binding.etEmailLogin.text.toString()
            val edtEmail= binding.tilEmail

            // jika email kosong
            if (email.isEmpty()){
                edtEmail.error= "Email Tidak Boleh Kosong"
                edtEmail.requestFocus()
                return@setOnClickListener
            }

            // Jika email tidak valid
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                edtEmail.error= "Email Tidak Valid"
                edtEmail.requestFocus()
                return@setOnClickListener
            }


            resetpassworddatabase()

            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful){
                    Toast.makeText(context, "Email Reset Password Telah Dikirim", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_resetPassFragment_to_newPasswordFragment)
                } else {
                    Toast.makeText(context, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }



    }

    fun resetpassworddatabase(){
        val email =  binding.etEmailLogin.text.toString()
        val forgotpass = PostForgotPass(email)
        userVm.forgotpass(forgotpass)

        userVm.responseforgotpass.observe(viewLifecycleOwner){
            if (it.message == "An email has been sent to ilhamok8@gmail.com with further instructions."){

                Toast.makeText(context, "Cek Email anda, Masukkan token dan password baru di aplikasi", Toast.LENGTH_SHORT).show()
            }
        }
    }


}