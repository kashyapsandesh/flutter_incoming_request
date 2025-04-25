import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:flutter_incoming_request/src/models/request_data.dart';
import 'package:flutter_incoming_request/src/services/request_service.dart';
import 'package:flutter_incoming_request/src/ui/layout_builder.dart';

class FlutterIncomingRequest {
  final RequestService _service = RequestService();
  final StreamController<RequestData> _requestController =
      StreamController<RequestData>.broadcast();

  Stream<RequestData> get onRequest => _requestController.stream;

  Future<void> show({
    required String name,
    String? number,
    String? routeName,
    Map<String, dynamic>? routeArgs,
    bool shouldStartApp = true,
  }) async {
    final data = RequestData(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      name: name,
      number: number,
      routeName: routeName,
      routeArgs: routeArgs,
      shouldStartApp: shouldStartApp,
    );
    await _service.show(data);
  }

  Future<void> hide(String id) async {
    await _service.hide(id);
  }

  void setActionHandler(
      Function(String id, NotificationAction action) handler) {
    _service.setActionHandler(handler);
  }

  void setOnRequestOpenedHandler(
      Function(String? routeName, Map<String, dynamic>? routeArgs) handler) {
    _service.setOnRequestOpenedHandler(handler);
  }

  void dispose() {
    _requestController.close();
  }
}
