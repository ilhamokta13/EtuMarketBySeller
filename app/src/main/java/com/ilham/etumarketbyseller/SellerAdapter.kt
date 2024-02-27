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

class SellerAdapter(private val context: Context, private val listproduct: List<DataAdmin>, private val homeVm : HomeViewModel, private val productVm: ProductViewModel) : RecyclerView.Adapter<SellerAdapter.ViewHolder>() {
    private val pref: SharedPreferences = context.getSharedPreferences("Success", Context.MODE_PRIVATE)
    class ViewHolder(var binding : ItemListBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bindseller(itemSeller:DataAllProduct){
//            binding.seller = itemSeller
////            binding.btnDetail.setOnClickListener {
////                val bundle = Bundle()
////                bundle.putSerializable("detail", itemSeller)
////                Navigation.findNavController(itemView).navigate(R.id.action_homeFragment_to_detailFragment)
////            }
//
//            binding.btnEdit.setOnClickListener {
//            var edit = Bundle()
//            edit.putSerializable("edit", itemSeller)
//            Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_editFragment)
//
//        }
//
//        binding.btnDetail.setOnClickListener{
//            var detail = Bundle()
//            detail.putSerializable("detail", itemSeller)
//            Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_detailFragment)
//        }
//
//        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view = ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.Judul.text = listproduct[position].nameProduct
        holder.binding.Harga.text = listproduct[position].price.toString()
//        holder.bindseller(listproduct[position])
        Glide.with(holder.itemView).load("https://7895jr9m-3000.asse.devtunnels.ms/uploads/${listproduct[position].image}").into(holder.binding.imgProduct)
//
        holder.binding.btnEdit.setOnClickListener {
            val id = listproduct[position].id
            var edit = Bundle()
            productVm.saveIdCart(id)
            edit.putString("idcart", id)
//            edit.putSerializable("edit", listproduct[position])
            Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_editFragment,edit)

        }

        holder.binding.btnDetail.setOnClickListener{
            var detail = Bundle()
            detail.putSerializable("detail", listproduct[position])
            Navigation.findNavController(it).navigate(R.id.action_homeFragment_to_detailFragment, detail)
        }

        holder.binding.btnDelete.setOnClickListener {
            val seller = listproduct[position]
            val token =  pref.getString("token", "").toString()
            homeVm.deleteproduct(token, seller.id)

//            homeVm.itemdeleteproduct.observe(LifecycleOwner){
//
//            }
            Toast.makeText(holder.itemView.context, "Item telah dihapus", Toast.LENGTH_SHORT).show()
        }









    }

    override fun getItemCount(): Int {
        return  listproduct.size

    }
}