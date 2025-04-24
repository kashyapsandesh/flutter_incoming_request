enum NotificationType { call, message, other }

class NotificationAction {
  final String id;
  final String text;
  final String? icon;
  final bool isDestructive;
  final bool isAuthenticationRequired;

  NotificationAction({
    required this.id,
    required this.text,
    this.icon,
    this.isDestructive = false,
    this.isAuthenticationRequired = false,
  });

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'text': text,
      'icon': icon,
      'isDestructive': isDestructive,
      'isAuthenticationRequired': isAuthenticationRequired,
    };
  }
}

class RequestData {
  final String id;
  final String name;
  final String? avatar;
  final String? number;
  final NotificationType type; // call, message, etc.
  final Duration? timeout; // Auto-dismiss after duration
  final String? extra;
  final Map<String, dynamic>? headers;
  final List<NotificationAction>? actions;
  final bool showInLockScreen;
  final bool showInApp;
  final String? routeName; // Route to navigate
  final Map<String, dynamic>? routeArgs; // Dynamic route args
  final bool shouldStartApp; // Force open app if closed

  RequestData({
    required this.id,
    required this.name,
    this.avatar,
    this.number,
    this.type = NotificationType.call,
    this.timeout,
    this.extra,
    this.headers,
    this.actions,
    this.showInLockScreen = true,
    this.showInApp = true,
    this.routeName,
    this.routeArgs,
    this.shouldStartApp = true,
  });

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'name': name,
      'avatar': avatar,
      'number': number,
      'type': type.name,
      'timeout': timeout?.inMilliseconds,
      'extra': extra,
      'headers': headers,
      'actions': actions?.map((e) => e.toMap()).toList(),
      'showInLockScreen': showInLockScreen,
      'showInApp': showInApp,
      'routeName': routeName,
      'routeArgs': routeArgs,
      'shouldStartApp': shouldStartApp,
    };
  }
}
