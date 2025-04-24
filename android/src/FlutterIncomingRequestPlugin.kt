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
      "show" -> showRequest(call.arguments as Map<String, Any>)
      "hide" -> hideRequest(call.arguments as String)
      else -> result.notImplemented()
    }
  }

  private fun showRequest(data: Map<String, Any>) {
    // Start foreground service & show notification
    val intent = Intent(context, RequestService::class.java).apply {
      putExtra("routeName", data["routeName"] as? String)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      context.startForegroundService(intent)
    } else {
      context.startService(intent)
    }
  }

  private fun hideRequest(id: String) {
    // Cancel notification
    (context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
      .cancel(id.hashCode())
  }
}