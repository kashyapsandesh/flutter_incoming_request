package com.kashyapsandesh.flutter_incoming_request

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import io.flutter.embedding.android.FlutterActivity

class RequestService : Service() {
    private val CHANNEL_ID = "incoming_request_channel"
    private val CHANNEL_NAME = "Incoming Requests"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val data = intent?.extras
        val notification = buildNotification(data)
        startForeground(1, notification)

        // Launch app if needed
        if (data?.getBoolean("shouldStartApp", true) == true) {
            launchMainActivity(intent)
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for incoming requests"
                setShowBadge(true)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(data: android.os.Bundle?): Notification {
        val name = data?.getString("name") ?: "Unknown"
        val number = data?.getString("number")
        val routeName = data?.getString("routeName")

        // Explicitly use the application context to get the package name
        val packageName = applicationContext.packageName
        val launchIntent = applicationContext.packageManager.getLaunchIntentForPackage(packageName)
        val mainActivityClassName = launchIntent?.component?.className ?: FlutterActivity::class.java.name

        val intent = Intent(this, Class.forName(mainActivityClassName)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("routeName", routeName)
            // Add any other data from the 'data' bundle as needed
            data?.let { putExtras(it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(name)
            .setContentText(number ?: "Incoming request")
            .setSmallIcon(android.R.drawable.ic_menu_call) // TODO: Replace with a proper app icon
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(true)
            .build()
    }

    private fun launchMainActivity(intent: Intent?) {
         val routeName = intent?.getStringExtra("routeName")

        // Explicitly use the application context to get the package name
        val packageName = applicationContext.packageName
        val launchIntent = applicationContext.packageManager.getLaunchIntentForPackage(packageName)
        val mainActivityClassName = launchIntent?.component?.className ?: FlutterActivity::class.java.name

        val appIntent = Intent(this, Class.forName(mainActivityClassName)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("routeName", routeName)
            // Add any other data from the original intent as needed
            intent?.extras?.let { putExtras(it) }
        }
        startActivity(appIntent)
    }
} 