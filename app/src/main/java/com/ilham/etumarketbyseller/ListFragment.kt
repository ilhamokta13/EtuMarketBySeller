package com.ilham.etumarketbyseller

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ilham.etumarketbyseller.databinding.FragmentListBinding


class ListFragment : Fragment() {
    lateinit var binding : FragmentListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(layoutInflater,container,false)
        return binding.root
    }


}