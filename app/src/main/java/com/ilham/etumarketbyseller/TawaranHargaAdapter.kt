package com.ilham.etumarketbyseller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.ilham.etumarketbyseller.databinding.ItemTawaranBinding
import com.ilham.etumarketbyseller.model.product.tawarharga.TawarProduct

class TawaranHargaAdapter(var listtawaran : List<TawarProduct>) : RecyclerView.Adapter<TawaranHargaAdapter.ViewHolder>() {




    class ViewHolder(var binding : ItemTawaranBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =  ItemTawaranBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listtawaran[position]
        val namaproduk = item.nama
        holder.binding.recNamaProduk.text = "Nama Produk : $namaproduk"
        val status = item.offer.status
        holder.binding.recStatusTawaran.text = "Status Tawaran :$status "
        val hargatawaran = item.price
        holder.binding.recHarga.text = "Harga Tawaran : $hargatawaran"

        holder.binding.cekstatus.setOnClickListener {
            val idproduct = item.id
            val offerId = item.offer.offerId
            val bundle = Bundle()
            bundle.putString("keytawar", idproduct)
            bundle.putString("offerId", offerId)
            Navigation.findNavController(it).navigate(R.id.action_tawaranHargaFragment_to_postTawarHargaFragment, bundle)
        }






    }

    override fun getItemCount(): Int {
        return listtawaran.size
    }



}