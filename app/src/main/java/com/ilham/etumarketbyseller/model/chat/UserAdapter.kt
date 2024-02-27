package com.ilham.etumarketbyseller.model.chat

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ilham.etumarketbyseller.ChatActivity
import com.ilham.etumarketbyseller.R
import com.ilham.etumarketbyseller.databinding.ItemUserBinding
import com.squareup.picasso.Picasso

class UserAdapter(private val context: Context, private val userList: ArrayList<User>) : RecyclerView.Adapter<UserAdapter.ViewHolder>(){
    class ViewHolder(var binding:ItemUserBinding) :RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = userList[position]
        holder.binding.userName.text = user.fullname.toString()
//        Glide.with(context).load(user.profileImage).placeholder(R.drawable.profile_image).into(holder.binding.userImage)
        Picasso.get().load("gs://etu-market---buyer.appspot.com/image/${user.profileImage}").into(holder.binding.userImage);

        holder.binding.layoutUser.setOnClickListener {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("userId",user.userId)
            intent.putExtra("fullname",user.fullname)
            context.startActivity(intent)

        }


    }

    override fun getItemCount(): Int {
      return userList.size
    }
}