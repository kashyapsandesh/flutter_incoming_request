package com.kashyapsandesh.flutter_incoming_request

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

class FlutterIncomingRequestPlugin : FlutterPlugin, MethodCallHandler {
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private lateinit var notificationHelper: NotificationHelper
    private var notificationId = 0

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        notificationHelper = NotificationHelper(context)
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "flutter_incoming_request")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "showNotification" -> {
                val title = call.argument<String>("title") ?: "Incoming Request"
                val content = call.argument<String>("content") ?: "You have a new request"
                
                val notification = notificationHelper.buildNotification(title, content)
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.notify(notificationId++, notification.build())
                
                result.success(null)
            }
            "show" -> {
                showRequest(call.arguments as? Map<String, Any>)
                result.success(null) // Acknowledge the call
            }
            "hide" -> {
                val args = call.arguments as? Map<String, Any>
                val id = args?.get("id") as? String
                if (id != null) {
                    hideRequest(id)
                }
                result.success(null) // Acknowledge the call
            }
            else -> result.notImplemented()
        }
    }

    private fun showRequest(data: Map<String, Any>?) {
        if (data == null) return
        // Start foreground service & show notification
        val intent = Intent(context, IncomingRequestService::class.java).apply {
            // Pass data to the service
            data.forEach { (key, value) ->
                when (value) {
                    is String -> putExtra(key, value)
                    is Boolean -> putExtra(key, value)
                    is Int -> putExtra(key, value)
                    is Long -> putExtra(key, value)
                    is Float -> putExtra(key, value)
                    is Double -> putExtra(key, value)
                    // Add other types as needed, or serialize complex data
                }
            }
            // Add routeName specifically if present
             if (data.containsKey("routeName")) {
                 putExtra("routeName", data["routeName"] as? String)
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    private fun hideRequest(id: String) {
        // Stop the service if it's running
        val serviceIntent = Intent(context, IncomingRequestService::class.java)
        context.stopService(serviceIntent)

        // Cancel notification
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1) // Use the same ID used in startForeground
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
} 