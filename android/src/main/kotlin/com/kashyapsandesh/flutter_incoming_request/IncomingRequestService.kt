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
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel

class IncomingRequestService : Service() {
    private lateinit var notificationManager: NotificationManager
    private var flutterEngine: FlutterEngine? = null
    private var methodChannel: MethodChannel? = null

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) return START_NOT_STICKY

        val name = intent.getStringExtra("name") ?: ""
        val number = intent.getStringExtra("number")
        val routeName = intent.getStringExtra("routeName")
        val routeArgs = intent.getSerializableExtra("routeArgs") as? HashMap<String, Any>
        val shouldStartApp = intent.getBooleanExtra("shouldStartApp", true)

        // Create notification
        val notification = createNotification(name, number, routeName, routeArgs, shouldStartApp)
        startForeground(1, notification)

        return START_NOT_STICKY
    }

    private fun createNotification(
        name: String,
        number: String?,
        routeName: String?,
        routeArgs: HashMap<String, Any>?,
        shouldStartApp: Boolean
    ): Notification {
        val title = "Incoming Request"
        val content = if (number != null) "$name ($number)" else name

        // Create intent to launch app
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("routeName", routeName)
            routeArgs?.let { putExtra("routeArgs", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Incoming Requests",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for incoming requests"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        flutterEngine?.destroy()
    }

    companion object {
        private const val CHANNEL_ID = "incoming_requests"
    }
} 