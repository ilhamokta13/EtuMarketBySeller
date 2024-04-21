package com.ilham.etumarketbyseller

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.messaging.FirebaseMessaging
import com.ilham.etumarketbyseller.databinding.FragmentListBinding
import com.ilham.etumarketbyseller.viewmodel.AdminViewModel
import com.ilham.etumarketbyseller.viewmodel.PaymentViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ListFragment : Fragment() {
    lateinit var binding : FragmentListBinding
    lateinit var adminVm : AdminViewModel
    lateinit var pref : SharedPreferences
    lateinit var pesananAdapter: PesananAdapter
    lateinit var paymentVm : PaymentViewModel

    // Keep track of the previous list of orders
    private var previousOrderCount = 0


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)
        val token = pref.getString("token", "").toString()

        // Initialize Firebase Messaging
        FirebaseMessaging.getInstance().subscribeToTopic("new_orders")

        pesananAdapter = PesananAdapter(ArrayList())

        getdata(token)

        binding.etSearchProduct.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                pesananAdapter.filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.recyclerViewItemRiwayat.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = pesananAdapter
        }
    }

    fun getdata(token: String) {
        adminVm = ViewModelProvider(this).get(AdminViewModel::class.java)
        paymentVm = ViewModelProvider(this).get(PaymentViewModel::class.java)

        adminVm.getpesanan(token)

        adminVm.datapesanan.observe(viewLifecycleOwner, Observer { newOrders ->
            // Check if new orders have been received
            if (newOrders.size > previousOrderCount) {
                // Trigger notification for new orders
                showNotification(
                    "New Order Received",
                    "You have ${newOrders.size - previousOrderCount} new order(s)"
                )
                // Update the previous order count
                previousOrderCount = newOrders.size
            }

            pesananAdapter.filteredList = newOrders
            pesananAdapter.listpesanan = newOrders

            binding.recyclerViewItemRiwayat.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            binding.recyclerViewItemRiwayat.adapter = pesananAdapter
        })
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "order_channel"
        val notificationId = 101

        val notificationBuilder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(requireContext())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Order Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }





}