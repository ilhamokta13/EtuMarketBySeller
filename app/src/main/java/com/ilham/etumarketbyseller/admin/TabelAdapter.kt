package com.ilham.etumarketbyseller.admin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.ilham.etumarketbyseller.R
import com.ilham.etumarketbyseller.model.profile.allprofile.Data

class TabelAdapter(context: Context, profiles: List<Data>) : ArrayAdapter<Data>(context, 0, profiles) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.item_profile, parent, false)
        }

        val currentProfile = getItem(position)

        val txtNo = listItemView!!.findViewById<TextView>(R.id.txtNo)
        txtNo.text = (position + 1).toString()

        val txtshopName = listItemView.findViewById<TextView>(R.id.txtshopName)
        txtshopName.text = currentProfile?.shopName

        val txtTelp = listItemView.findViewById<TextView>(R.id.txtTelp)
        txtTelp.text = currentProfile?.telp

        val txtNamaKetua = listItemView.findViewById<TextView>(R.id.txtNamaKetua)
        txtNamaKetua.text = currentProfile?.fullName

        return listItemView!!
    }
}