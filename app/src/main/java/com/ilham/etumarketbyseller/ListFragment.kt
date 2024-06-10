package com.ilham.etumarketbyseller

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.zzl.getToken
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.ilham.etumarketbyseller.constant.FirebaseService
import com.ilham.etumarketbyseller.constant.MyFirebaseMessagingService
import com.ilham.etumarketbyseller.databinding.FragmentListBinding
import com.ilham.etumarketbyseller.model.BaseApplication
import com.ilham.etumarketbyseller.model.chat.NotificationData
import com.ilham.etumarketbyseller.model.chat.PushNotification
import com.ilham.etumarketbyseller.model.chat.RetrofitInstance
import com.ilham.etumarketbyseller.viewmodel.AdminViewModel
import com.ilham.etumarketbyseller.viewmodel.PaymentViewModel
import com.ilham.etumarketbyseller.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@AndroidEntryPoint
class ListFragment : Fragment() {
    lateinit var binding : FragmentListBinding
    lateinit var adminVm : AdminViewModel
    lateinit var pref : SharedPreferences
    lateinit var pesananAdapter: PesananAdapter


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
        MyFirebaseMessagingService.sharedPref = requireActivity().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        pesananAdapter = PesananAdapter(ArrayList(), requireContext())

        FirebaseApp.initializeApp(requireContext())

        getdata(token)

        binding.etSearchProduct.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                pesananAdapter.filter(binding.etSearchProduct.text.toString(), binding.statusFilterSpinner.selectedItem.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.recyclerViewItemRiwayat.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = pesananAdapter
        }

        binding.statusFilterSpinner.adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.status_array,
            android.R.layout.simple_spinner_item
        )

        binding.statusFilterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val status = parent.getItemAtPosition(position).toString()
                pesananAdapter.filter(binding.etSearchProduct.text.toString(), status)
//                pesananAdapter.filter(status)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null && !TextUtils.isEmpty(task.result)) {
                        val token: String = task.result!!
                        // Lakukan sesuatu dengan token jika perlu
                    }
                }
            }




    }

    @SuppressLint("NotifyDataSetChanged")
    fun getdata(token: String) {
        adminVm = ViewModelProvider(this).get(AdminViewModel::class.java)
        adminVm.getpesanan(token)

        adminVm.datapesanan.observe(viewLifecycleOwner, Observer { newOrders ->
            if (newOrders.size > previousOrderCount) {
                val newOrderCount = newOrders.size - previousOrderCount
                previousOrderCount = newOrders.size


                // Show notification for new orders
                showNotification(newOrderCount)



            }

            pesananAdapter.filteredList = newOrders
            pesananAdapter.listpesanan = newOrders
            pesananAdapter.notifyDataSetChanged()
        })
    }


    private fun showNotification(newOrderCount: Int) {
        val intent = Intent(requireContext(), ListFragment::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val channelId = getString(R.string.default_notification_channel_id)
        val notificationBuilder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24) // Use your own icon here
            .setContentTitle(getString(R.string.fcm_message))
            .setContentText("You have $newOrderCount new orders.")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Order Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }







}