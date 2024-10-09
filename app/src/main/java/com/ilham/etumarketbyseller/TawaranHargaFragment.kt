package com.ilham.etumarketbyseller

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.messaging.FirebaseMessaging
import com.ilham.etumarketbyseller.constant.MyFirebaseMessagingService
import com.ilham.etumarketbyseller.databinding.FragmentTawaranHargaBinding
import com.ilham.etumarketbyseller.model.product.tawarharga.TawarProduct
import com.ilham.etumarketbyseller.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TawaranHargaFragment : Fragment() {
   lateinit var binding : FragmentTawaranHargaBinding
    lateinit var pref: SharedPreferences
    lateinit var productVm: ProductViewModel
    private lateinit var tawaranhargaadapter: TawaranHargaAdapter
    private var itemtawar: List<TawarProduct> = listOf()
    private var previousBidCount = 0



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTawaranHargaBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = requireActivity().getSharedPreferences("Success", Context.MODE_PRIVATE)!!
        MyFirebaseMessagingService.sharedPref = requireActivity().getSharedPreferences("Berhasil", Context.MODE_PRIVATE)
        productVm = ViewModelProvider(this).get(ProductViewModel::class.java)
        val token = pref.getString("token", "").toString()
        tawaranhargaadapter = TawaranHargaAdapter(itemtawar)

        val getIdproducts = arguments?.getString("idtawar")

        productVm.tawarharga(token,getIdproducts!!)

        productVm.datatawarharga.observe(viewLifecycleOwner){
            binding.rvMain.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false
            )

            if (it!= null) {
                binding.rvMain.adapter = TawaranHargaAdapter(it)
                if (it.size > previousBidCount) {
                    sendNotification("New Tawar Harga", "Ada seseorang yang menawar produk anda")
                    previousBidCount = it.size
                }
            }
        }

        initializeFirebaseMessaging()


    }

    private fun initializeFirebaseMessaging() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result != null && !TextUtils.isEmpty(task.result)) {
                        val token: String = task.result!!
                        Log.d("FCM Initialization", "Success: $token")
                    }
                }
            }
    }


    private fun sendNotification(title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(requireContext(), "BID_CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "BID_CHANNEL_ID", "Bid Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.enableVibration(true)
            manager.createNotificationChannel(channel)
        }

        manager.notify(1, notification)
    }


}