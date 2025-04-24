package com.kashyapsandesh.flutter_incoming_request

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class RequestService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        
        val notification = NotificationCompat.Builder(this, "incoming_request_channel")
            .setContentTitle("Incoming Request")
            .setContentText("Handling incoming request")
            .setSmallIcon(android.R.drawable.ic_menu_call)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1, notification)

        // Handle the request and launch activity if needed
        if (intent?.getBooleanExtra("shouldStartApp", true) == true) {
            launchMainActivity(intent)
        }

        return START_NOT_STICKY
    }

    private fun launchMainActivity(intent: Intent) {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("from_notification", true)
            putExtra("routeName", intent.getStringExtra("routeName"))
        }
        startActivity(launchIntent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "incoming_request_channel",
                "Incoming Requests",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Channel for incoming requests"
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}