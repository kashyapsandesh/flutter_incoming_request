library flutter_incoming_request;

import 'package:flutter/services.dart';

export 'src/flutter_incoming_request.dart';
export 'src/models/request_data.dart';
export 'src/services/request_service.dart';
export 'src/ui/layout_builder.dart';

class FlutterIncomingRequest {
  static const MethodChannel _channel =
      MethodChannel('flutter_incoming_request');

  /// Shows a notification with the given title and content.
  ///
  /// [title] - The title of the notification. Defaults to "Incoming Request" if not provided.
  /// [content] - The content/body of the notification. Defaults to "You have a new request" if not provided.
  static Future<void> showNotification({
    String? title,
    String? content,
  }) async {
    await _channel.invokeMethod('showNotification', {
      'title': title,
      'content': content,
    });
  }
}
