package com.ilham.etumarketbyseller

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.ilham.etumarketbyseller.constant.FirebaseService
import com.ilham.etumarketbyseller.constant.MyFirebaseMessagingService
import com.ilham.etumarketbyseller.databinding.FragmentListBinding
import com.ilham.etumarketbyseller.model.chat.NotificationData
import com.ilham.etumarketbyseller.model.chat.PushNotification
import com.ilham.etumarketbyseller.model.chat.RetrofitInstance
import com.ilham.etumarketbyseller.viewmodel.AdminViewModel
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

    private val orderReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Refresh data when new order is received
            val token = pref.getString("token", "").toString()
            getdata(token)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)
        MyFirebaseMessagingService.sharedPref = requireActivity().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        val token = pref.getString("token", "").toString()
        requireActivity().registerReceiver(orderReceiver, IntentFilter("com.ilham.etumarketbyseller.ListFragment"))

        pesananAdapter = PesananAdapter(ArrayList(), requireContext())

        initializeFirebaseMessaging()
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





    }

    private fun initializeFirebaseMessaging() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null && !TextUtils.isEmpty(task.result)) {
                        val token: String = task.result!!
                        Log.d("Inisisalisasi FCM", "Berhasil $task.result")
                        Log.d("FCM Token", "Token: $token")
                        // Lakukan sesuatu dengan token jika perlu
                    }
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Unregister receiver
        requireActivity().unregisterReceiver(orderReceiver)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getdata(token: String) {
        adminVm = ViewModelProvider(this).get(AdminViewModel::class.java)
        adminVm.getpesanan(token)

        adminVm.datapesanan.observe(viewLifecycleOwner, Observer { newOrders ->
            if (newOrders.size > previousOrderCount) {
                val notificationData = NotificationData(
                    title = "Ada pesanan masuk",
                    message = "Ada pesanan baru",
                    // Optionally, you can include imageUrl if needed
                    imageUrl = null
                )
                sendDetectionNotification("Ada pesanan masuk", 1.0f)
                previousOrderCount = newOrders.size
            }

            pesananAdapter.filteredList = newOrders
            pesananAdapter.listpesanan = newOrders
            pesananAdapter.notifyDataSetChanged()
        })
    }



    private fun sendDetectionNotification(detection: String, confidence: Float) {
        if (confidence > 0.5) {
            val intent = Intent(context, MainActivity::class.java)
            val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, flags)
            val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = NotificationCompat.Builder(requireContext(), "CHANNEL_ID")
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setVibrate(longArrayOf(1000))
                .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentText(detection)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    "CHANNEL_ID", "Pesanan Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationChannel.enableVibration(true)
                notificationChannel.vibrationPattern = longArrayOf(1000)
                manager.createNotificationChannel(notificationChannel)
            }
            manager.notify(2, notification.build())
        }
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            withContext(Dispatchers.IO) {

                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
//                    Log.d("TAG", "Response: ${Gson().toJson(response)}")
                } else {
                    Log.e("TAG", response.errorBody()!!.string())
                }


            }
        } catch(e: Exception) {
            Log.e("TAG", e.toString())
        }
    }






















}