package com.ilham.etumarketbyseller

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilham.etumarketbyseller.databinding.ItemPesananBinding
import com.ilham.etumarketbyseller.model.product.listpesanan.DataPesanan

class PesananAdapter( var listpesanan : List<DataPesanan>, var context: Context ) : RecyclerView.Adapter<PesananAdapter.ViewHolder>(){
     var filteredList: List<DataPesanan> = listpesanan

    var redItems: MutableList<DataPesanan> = mutableListOf()
    var blueItems: MutableList<DataPesanan> = mutableListOf()

    var statusFilter: String? = null

    class ViewHolder(var binding : ItemPesananBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =  ItemPesananBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listriwayatpesanan = listpesanan[position]

        val item = filteredList[position]

//        val item = if (position < blueItems.size) blueItems[position] else redItems[position - blueItems.size]
        val product = item.products.firstOrNull() ?: return
        holder.binding.recTittleRiwayat.text = product.productID.nameProduct

        // Filter berdasarkan status
        fun filterstatus(){
            if (statusFilter!!.isNotEmpty() && product.status != statusFilter) {
                return
            }
        }


//        holder.binding.recHargaItemRiwayat.text = product.total.toString()
        holder.binding.recQuantityRiwayat.text = product.quantity.toString()
        val harga = product.productID.price
        val quantity = product.quantity
        val total = harga * quantity
        holder.binding.recHargaItemRiwayat.text = total.toString()
        holder.binding.recNamaPembeli.text = item.user.fullName
        holder.binding.recTelpon.text = item.user.telp


        holder.binding.recStatusPembayaran.text= item.status
        holder.binding.recStatusPesanan.text = product.status

        holder.binding.recOngkir.text = item.shippingCost.toString()


        Glide.with(holder.itemView.context)
            .load("https://7895jr9m-3000.asse.devtunnels.ms/uploads/${product.productID.image}")
            .into(holder.binding.imagePesananRiwayat)

        holder.binding.recCard.setOnClickListener {
            val kodetransaksi = item.kodeTransaksi
            val ProductId = product.productID.id
            val bundle = Bundle()
            bundle.putString("kodetrans", kodetransaksi)
            bundle.putString("productid", ProductId)
            Navigation.findNavController(it).navigate(R.id.action_listFragment_to_listDetailFragment, bundle)

        }

        holder.binding.lihatshareloc.setOnClickListener {

            val destination = filteredList[position].destination

            // Create a Uri from the destination coordinates
            val gmmIntentUri = Uri.parse("google.navigation:q=${destination.latitude},${destination.longitude}")

            // Create an Intent to open Google Maps with the Uri
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps") // Specify the Google Maps package

            // Verify that Google Maps is installed before starting the Intent
            if (mapIntent.resolveActivity(context.packageManager) != null) {
                startActivity(context, mapIntent, null)
            } else {
                // If Google Maps is not installed, you can prompt the user to install it or handle the situation accordingly
                // For example, you can open the destination in a web browser
            }

//            Navigation.findNavController(it).navigate(R.id.action_listFragment_to_lihatLokasiFragment)
        }


//        // Check if the order status is "delivered"
//        if (product.status == "Selesai") {
//            holder.binding.itemlist.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
//        } else {
//            // If not delivered, set the default background color
//            holder.binding.itemlist.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.blue))
//        }

    }


    override fun getItemCount(): Int {
        return filteredList.size
    }

//    fun filter(text: String) {
//        listpesanan = if (text.isEmpty()) {
//            filteredList
//        } else {
//            val tempList = ArrayList<DataPesanan>()
//            for (item in filteredList) {
//                if (item.products.firstOrNull()?.productID!!.nameProduct!!
//                        .contains(text, ignoreCase = true)
//                ) {
//                    tempList.add(item)
//                }
//            }
//            tempList
//        }
//        separateItemsByColor()
//        notifyDataSetChanged()
//    }

    fun filter(text: String, status: String?) {
        statusFilter = status
        filteredList = if (text.isEmpty()) {
            listpesanan.filter { item ->
                status == null || item.products.any { product -> product.status == status }
            }
        } else {
            listpesanan.filter { item ->
                (status == null || item.products.any { product ->
                    product.status == status && product.productID.nameProduct.contains(
                        text,
                        ignoreCase = true
                    )
                })
            }
        }
        notifyDataSetChanged()
    }

    fun setAllHistory(history: List<DataPesanan>) {
        listpesanan = history
        filter("", statusFilter) // Terapkan filter yang sudah ada
    }



    // Memisahkan item berdasarkan warna
//    fun separateItemsByColor() {
//        redItems.clear()
//        blueItems.clear()
//        for (item in listpesanan) {
//            if (item.products.firstOrNull()?.status == "Selesai") {
//                redItems.add(item)
//            } else {
//                blueItems.add(item)
//            }
//        }
//    }

}