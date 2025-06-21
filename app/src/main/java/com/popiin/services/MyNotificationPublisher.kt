package com.popiin.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.popiin.R
import com.popiin.SplashActivity

public class MyNotificationPublisher(private val mContext: Context) : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = "notification-id"
        const val NOTIFICATION = "notification"
    }

    override fun onReceive(context: Context, intent: Intent) {

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification: Notification? = intent.getParcelableExtra(NOTIFICATION)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("1001", "Popiin", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        val num = System.currentTimeMillis().toInt()
        notification?.let { notificationManager.notify(num, it) }
    }

    @SuppressLint("WrongConstant")
    fun sendNotification(messageTitle: String?, messageBody: String?) {
        val intent = Intent(mContext, SplashActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val channelId = mContext.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val notificationBuilder = NotificationCompat.Builder(mContext, channelId)
            .setSmallIcon(R.mipmap.ic_app_icon)
            .setContentTitle(messageTitle)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Popiin", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val num = System.currentTimeMillis().toInt()
        notificationManager.notify(num, notificationBuilder.build())
    }

}
