package com.ilham.etumarketbyseller.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ilham.etumarketbyseller.R
import com.ilham.etumarketbyseller.databinding.ItemAdminBinding
import com.ilham.etumarketbyseller.model.pendapatan.DataToko

class AdminAdapter(private val listadmin: List<DataToko>) : RecyclerView.Adapter<AdminAdapter.ViewHolder>() {
    private var selectedDetailData: List<DataToko> = emptyList()
     private lateinit var detailAdapter: ListAdminAdapter


    class ViewHolder(var binding : ItemAdminBinding) : RecyclerView.ViewHolder(binding.root)
        private var detailPopupWindow: PopupWindow? = null



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemAdminBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataItem = listadmin[position]
        val transaksi = dataItem.transaksi.firstOrNull() ?: return
        holder.binding.NamaToko.text = dataItem.sellerName
        holder.binding.TotalPendapatan.text = dataItem.totalPendapatan.toString()
        holder.binding.Kategori.text = transaksi.product.category

        holder.binding.dropdownButton.setOnClickListener {
            if (holder.binding.detailRecyclerView.visibility == View.VISIBLE) {
                holder.binding.detailRecyclerView.visibility = View.GONE
            } else {
                holder.binding.detailRecyclerView.visibility = View.VISIBLE
                showDetailPopup(holder.binding.root, position) // Menggunakan posisi item
            }
        }

        holder.binding.detailRecyclerView.layoutManager = LinearLayoutManager(holder.binding.root.context)
        val detailAdapter = ListAdminAdapter(listOf()) // Awalnya kosong
        holder.binding.detailRecyclerView.adapter = detailAdapter

        holder.binding.btnDetail.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_detailAdminFragment_to_grafikFragment)
        }





//        val detailAdapter = DetailAdminAdapter(selectedDetailData)
//        holder.binding.detailRecyclerView.apply {
//            layoutManager = LinearLayoutManager(context)
//            adapter = detailAdapter
//        }
    }


    private fun showDetailPopup(anchorView: View, position: Int) {
        val detailRecyclerView = RecyclerView(anchorView.context)
        detailRecyclerView.layoutManager = LinearLayoutManager(anchorView.context, LinearLayoutManager.VERTICAL, false)

        // Ambil transaksi untuk admin yang dipilih
        val selectedAdmin = listadmin[position]
        val selectedTransaksi = selectedAdmin.transaksi

        val detailAdapter = ListAdminAdapter(selectedTransaksi)
        detailRecyclerView.adapter = detailAdapter

        detailPopupWindow = PopupWindow(detailRecyclerView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        detailPopupWindow?.showAsDropDown(anchorView)
    }




    override fun getItemCount(): Int {
        return listadmin.size
    }



}
