package com.popiin.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.os.Build
import android.util.Log
import com.popiin.util.PrefManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.popiin.R
import com.popiin.activity.HomeActivity

class FireBaseMessagingService : FirebaseMessagingService() {
    companion object {
        private const val TAG = "MyFirebaseMsgService"
        private const val CHANNEL_ID = "popiin_notifications"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "PopiIn Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for PopiIn app notifications"
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private lateinit var myNotificationPublisher: MyNotificationPublisher

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Displaying data in log

        Log.d(TAG, "Notification Message DATA: ${remoteMessage.data}")
        remoteMessage.notification?.let {
            Log.d(TAG, "Notification Message TITLE: ${it.title}")
            Log.d(TAG, "Notification Message BODY: ${it.body}")
            sendNotification(it.title, it.body, remoteMessage.data)
        }
    }

    private fun sendNotification(title: String?, body: String?, data: Map<String, String>) {
        val intent = Intent(this, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_app_icon) // Ensure you have a valid icon in res/drawable
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed Firebase token: $token")
        PrefManager.setFirebaseToken(token)
    }
}
