package com.ilham.etumarketbyseller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilham.etumarketbyseller.databinding.ItemListBinding
import com.ilham.etumarketbyseller.model.product.allproduct.Data

class SellerAdapter(private var listproduct: List<Data>) : RecyclerView.Adapter<SellerAdapter.ViewHolder>() {
    class ViewHolder(var binding : ItemListBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.Judul.text = listproduct[position].nameProduct
        holder.binding.Harga.text = listproduct[position].price.toString()
        Glide.with(holder.itemView).load(listproduct[position].image).into(holder.binding.imgProduct)

        holder.binding.btnEdit.setOnClickListener {
            var edit = Bundle()
            edit.putSerializable("edit", listproduct[position])
            Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_editFragment)

        }

        holder.binding.btnDetail.setOnClickListener{
            var detail = Bundle()
            detail.putSerializable("detail", listproduct[position])
            Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_detailFragment)
        }




    }

    override fun getItemCount(): Int {
        return  listproduct.size

    }
}