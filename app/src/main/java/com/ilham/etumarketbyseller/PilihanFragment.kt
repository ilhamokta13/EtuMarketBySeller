package com.ilham.etumarketbyseller

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.ilham.etumarketbyseller.databinding.FragmentPilihanBinding

class PilihanFragment : Fragment() {

    lateinit var binding : FragmentPilihanBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentPilihanBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginadmin.setOnClickListener {
            findNavController().navigate(R.id.action_pilihanFragment_to_loginAdminFragment)
        }

        binding.loginseller.setOnClickListener {
            findNavController().navigate(R.id.action_pilihanFragment_to_loginFragment)
        }
    }


}