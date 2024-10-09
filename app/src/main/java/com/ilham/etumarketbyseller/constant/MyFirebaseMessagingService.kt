package com.ilham.etumarketbyseller.constant

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ilham.etumarketbyseller.ChatActivity.Companion.CHANNEL_ID
import com.ilham.etumarketbyseller.ListFragment
import com.ilham.etumarketbyseller.MainActivity
import com.ilham.etumarketbyseller.R
import com.ilham.etumarketbyseller.UserFragment
import kotlin.random.Random


class MyFirebaseMessagingService: FirebaseMessagingService() {
    val CHANNEL_ID = "my_notification_channel"
    companion object{
        var sharedPref: SharedPreferences? = null

        var token:String?
            get(){
                return sharedPref?.getString("token","")
            }
            set(value){
                sharedPref?.edit()?.putString("token",value)?.apply()
            }
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        token = p0
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        val intent = Intent(this, ListFragment::class.java)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val notification = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle(p0.data["title"])
            .setContentText(p0.data["message"])
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId,notification)


    }




    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "ChannelFirebaseChat"
            val channel = NotificationChannel(CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                description = "MY FIREBASE CHAT DESCRIPTION"
                enableLights(true)
                lightColor = Color.WHITE
            }
            notificationManager.createNotificationChannel(channel)
        }
    }


    private fun sendDetectionNotification(title: String?, message: String?) {
        if (title != null && message != null) {
            val intent = Intent(this, MainActivity::class.java)
            val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, flags)
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setVibrate(longArrayOf(1000))
                .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    CHANNEL_ID, "Pesanan Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationChannel.enableVibration(true)
                notificationChannel.vibrationPattern = longArrayOf(1000)
                manager.createNotificationChannel(notificationChannel)
            }
            manager.notify(2, notification.build())
        }
    }

}
