package com.ilham.etumarketbyseller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilham.etumarketbyseller.databinding.ItemPesananBinding
import com.ilham.etumarketbyseller.model.product.listpesanan.DataPesanan

class PesananAdapter( var listpesanan : List<DataPesanan> ) : RecyclerView.Adapter<PesananAdapter.ViewHolder>(){
     var filteredList: List<DataPesanan> = ArrayList()

    class ViewHolder(var binding : ItemPesananBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =  ItemPesananBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listriwayatpesanan = listpesanan[position]
        val product = listriwayatpesanan.products.firstOrNull() ?: return
        holder.binding.recTittleRiwayat.text = product.productID.nameProduct
//        holder.binding.recHargaItemRiwayat.text = product.total.toString()
        holder.binding.recQuantityRiwayat.text = product.quantity.toString()
        val harga = product.productID.price
        val quantity = product.quantity
        val total = harga * quantity
        holder.binding.recHargaItemRiwayat.text = total.toString()
        holder.binding.recNamaPembeli.text = listriwayatpesanan.user.fullName
        holder.binding.recTelpon.text = listriwayatpesanan.user.telp

        Glide.with(holder.itemView.context)
            .load("https://7895jr9m-3000.asse.devtunnels.ms/uploads/${product.productID.image}")
            .into(holder.binding.imagePesananRiwayat)

        holder.binding.recCard.setOnClickListener {
            val kodetransaksi = listriwayatpesanan.kodeTransaksi
            val ProductId = product.productID.id
            val bundle = Bundle()
            bundle.putString("kodetrans", kodetransaksi)
            bundle.putString("productid", ProductId)
            Navigation.findNavController(it).navigate(R.id.action_listFragment_to_listDetailFragment, bundle)



        }

    }


    override fun getItemCount(): Int {
       return listpesanan.size
    }

    fun filter(text: String) {
        listpesanan = if (text.isEmpty()) {
           filteredList
        } else {
             val tempList = ArrayList<DataPesanan>()
            for (item in filteredList) {
                // Ganti dengan kriteria pencarian yang sesuai dengan kebutuhan Anda
                if (item.products.firstOrNull()?.productID!!.nameProduct!!.contains(text, ignoreCase = true) == true) {
                    tempList.add(item)
                }
            }
            tempList
        }
        notifyDataSetChanged()
    }

}