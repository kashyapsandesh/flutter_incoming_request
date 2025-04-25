import 'dart:io' show Platform;

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter_incoming_request/src/models/request_data.dart';
import 'package:flutter_incoming_request/src/ui/layout_builder.dart';

class PlatformCheck {
  static bool get isAndroid => Platform.isAndroid;
  static bool get isIOS => Platform.isIOS;
}

class RequestService {
  static const MethodChannel _channel =
      MethodChannel('flutter_incoming_request');
  static Function(String id, NotificationAction action)? _actionHandler;
  static Function(String? routeName, Map<String, dynamic>? routeArgs)?
      _routeHandler;

  Future<void> show(RequestData data, {CustomLayoutBuilder? builder}) async {
    try {
      final args = data.toMap();
      if (builder != null && PlatformCheck.isAndroid) {
        args['layout'] = builder.buildLayout(data, (action) {});
      }
      await _channel.invokeMethod('show', args);
    } on PlatformException catch (e) {
      debugPrint("Failed to show request: ${e.message}");
    }
  }

  Future<void> hide(String id) async {
    try {
      await _channel.invokeMethod('hide', {'id': id});
    } on PlatformException catch (e) {
      debugPrint("Failed to hide request: ${e.message}");
    }
  }

  void setActionHandler(
      Function(String id, NotificationAction action) handler) {
    _actionHandler = handler;
    _channel.setMethodCallHandler(_handleMethodCall);
  }

  void setOnRequestOpenedHandler(
      Function(String?, Map<String, dynamic>?) handler) {
    _routeHandler = handler;
  }

  Future<dynamic> _handleMethodCall(MethodCall call) async {
    switch (call.method) {
      case 'action':
        if (_actionHandler != null) {
          final id = call.arguments['id'] as String;
          final action = NotificationAction(
            id: call.arguments['actionId'],
            text: call.arguments['text'] ?? '',
          );
          _actionHandler!(id, action);
        }
        break;
      case 'route':
        if (_routeHandler != null) {
          final routeName = call.arguments['routeName'] as String?;
          final routeArgs =
              call.arguments['routeArgs'] as Map<String, dynamic>?;
          _routeHandler!(routeName, routeArgs);
        }
        break;
    }
  }
}
