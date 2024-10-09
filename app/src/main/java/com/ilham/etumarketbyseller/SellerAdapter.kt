package com.ilham.etumarketbyseller

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilham.etumarketbyseller.databinding.ItemListBinding
import com.ilham.etumarketbyseller.model.product.getproductadmin.DataAdmin
import com.ilham.etumarketbyseller.viewmodel.HomeViewModel
import com.ilham.etumarketbyseller.viewmodel.ProductViewModel

class SellerAdapter(private val context: Context, var listproduct: List<DataAdmin>, private val homeVm : HomeViewModel, private val productVm: ProductViewModel) : RecyclerView.Adapter<SellerAdapter.ViewHolder>() {
    private val pref: SharedPreferences = context.getSharedPreferences("Success", Context.MODE_PRIVATE)

    var filteredList: List<DataAdmin> = ArrayList()
    class ViewHolder(var binding : ItemListBinding) : RecyclerView.ViewHolder(binding.root) {


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.Judul.text = listproduct[position].nameProduct
        holder.binding.Harga.text = listproduct[position].price.toString()
        Glide.with(holder.itemView).load("https://f31jwrgg-3000.asse.devtunnels.ms/uploads/${listproduct[position].image}").into(holder.binding.imgProduct)
        holder.binding.btnEdit.setOnClickListener {
            val id = listproduct[position].id
            var edit = Bundle()
            productVm.saveIdCart(id)
            edit.putString("idcart", id)
            Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_editFragment,edit)

        }

        holder.binding.btnDetail.setOnClickListener{
            var detail = Bundle()
            detail.putParcelable("detail", listproduct[position])
            Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_detailFragment, detail)
        }

        holder.binding.btnDelete.setOnClickListener {
            val seller = listproduct[position]
            val token =  pref.getString("token", "").toString()
            homeVm.deleteproduct(token, seller.id)
            Toast.makeText(holder.itemView.context, "Item telah dihapus", Toast.LENGTH_SHORT).show()
        }









    }
    override fun getItemCount(): Int {
        return  listproduct.size

    }

    fun filter(text: String) {
        listproduct = if (text.isEmpty()) {
            filteredList
        } else {
            val tempList = ArrayList<DataAdmin>()
            for (item in filteredList) {
                // Ganti dengan kriteria pencarian yang sesuai dengan kebutuhan Anda
                if (item.nameProduct.contentEquals(text, ignoreCase = true) == true){
                    tempList.add(item)
                }
            }
            tempList
        }
        notifyDataSetChanged()
    }
}