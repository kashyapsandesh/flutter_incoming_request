package com.kashyapsandesh.flutter_incoming_request

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

class FlutterIncomingRequestPlugin : FlutterPlugin, MethodCallHandler {
    private lateinit var channel: MethodChannel
    private lateinit var context: Context

    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(binding.binaryMessenger, "flutter_incoming_request")
        channel.setMethodCallHandler(this)
        context = binding.applicationContext
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        when (call.method) {
            "show" -> {
                try {
                    showRequest(call.arguments as Map<String, Any>)
                    result.success(null)
                } catch (e: Exception) {
                    result.error("SHOW_ERROR", e.message, null)
                }
            }
            "hide" -> {
                try {
                    hideRequest(call.arguments as String)
                    result.success(null)
                } catch (e: Exception) {
                    result.error("HIDE_ERROR", e.message, null)
                }
            }
            else -> result.notImplemented()
        }
    }

    private fun showRequest(data: Map<String, Any>) {
        val intent = Intent(context, RequestService::class.java).apply {
            putExtra("routeName", data["routeName"] as? String)
            putExtra("shouldStartApp", data["shouldStartApp"] as? Boolean ?: true)
            // Add other extras as needed
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, intent)
        } else {
            context.startService(intent)
        }
    }

    private fun hideRequest(id: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id.hashCode())
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }
}