package com.ilham.etumarketbyseller.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilham.etumarketbyseller.databinding.ItemDetailAdminBinding
import com.ilham.etumarketbyseller.model.pendapatan.TransaksiToko

class ListAdminAdapter(var listdetailadmin : List<TransaksiToko>) : RecyclerView.Adapter<ListAdminAdapter.ViewHolder>() {
    class ViewHolder(var binding : ItemDetailAdminBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemDetailAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataItem = listdetailadmin[position]

       holder.binding.recTittleRiwayat.text =dataItem.product.nameProduct
        holder.binding.recHargaItemRiwayat.text = dataItem.product.price.toString()
        holder.binding.recNamaPembeli.text = dataItem.status
//        holder.binding.recTotalHarga.text = dataItem.products.total.toString()
        val quantity = dataItem.products.quantity
        val harga  = dataItem.product.price
        val total = quantity * harga
        holder.binding.recTotalHarga.text = total.toString()
        holder.binding.recQuantityRiwayat.text = dataItem.products.quantity.toString()
        holder.binding.recStatusBarang.text = dataItem.products.status

        Glide.with(holder.itemView.context)
            .load("https://7895jr9m-3000.asse.devtunnels.ms/uploads/${dataItem.product.image}")
            .into(holder.binding.imagePesananRiwayat)




    }

    override fun getItemCount(): Int {
       return listdetailadmin.size
    }

//    fun updateData(newData: List<DataToko>) {
//        listdetailadmin = newData
//    }


}