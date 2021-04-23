package com.example.forgiphyapp.workManager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.forgiphyapp.R
import javax.inject.Inject

class Notification @Inject constructor(private val appContext: Context) {

    fun showNotification(title: String, text: String, intent: Intent, notifyId: String, id: Int) {

        createNotificationChannel(notifyId)

        val pendingIntent = PendingIntent.getActivity(appContext, 0, intent, 0)

        val builder = NotificationCompat.Builder(appContext, notifyId)
            .setSmallIcon(R.drawable.loading_animation)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(appContext)) {
            notify(id, builder.build())
        }
    }

    private fun createNotificationChannel(notifyId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Giphy App"
            val descriptionText = "Clear Database"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(notifyId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}