import 'dart:async';
import 'package:flutter/services.dart';
import 'package:flutter_incoming_request/flutter_incoming_request.dart';

class RequestHandler {
  static const EventChannel _eventChannel =
      EventChannel('flutter_incoming_request/events');
  static StreamSubscription? _subscription;
  static Function(String, Map<String, dynamic>)? _onRequestCallback;

  static void initialize({
    required Function(String, Map<String, dynamic>) onRequest,
  }) {
    _onRequestCallback = onRequest;
    _startListening();
  }

  static void _startListening() {
    _subscription?.cancel();
    _subscription =
        _eventChannel.receiveBroadcastStream().listen((dynamic event) {
      if (event is Map) {
        final eventType = event['event'] as String;
        final data = event['data'] as Map<String, dynamic>;

        if (eventType == 'request' && _onRequestCallback != null) {
          _onRequestCallback!(eventType, data);
        }
      }
    }, onError: (dynamic error) {
      print('Error listening to events: $error');
    });
  }

  static void dispose() {
    _subscription?.cancel();
    _subscription = null;
    _onRequestCallback = null;
  }
}
