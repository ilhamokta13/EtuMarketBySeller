package com.ilham.etumarketbyseller.buyer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilham.etumarketbyseller.databinding.ItemProductBinding
import com.ilham.etumarketbyseller.model.product.allproduct.DataAllProduct

class BuyerAdapter(private val listbuyer: List<DataAllProduct> ) :  RecyclerView.Adapter<BuyerAdapter.ViewHolder>(){
    class ViewHolder(var binding : ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvJudulProduct.text = listbuyer[position].nameProduct
        holder.binding.tvTglProduct.text = listbuyer[position].releaseDate
        holder.binding.tvHargaProduct.text = listbuyer[position].price.toString()
        Glide.with(holder.itemView).load("https://7895jr9m-3000.asse.devtunnels.ms/uploads/${listbuyer[position].image}").into(holder.binding.imgProduct)
    }

    override fun getItemCount(): Int {
        return listbuyer.size
    }
}