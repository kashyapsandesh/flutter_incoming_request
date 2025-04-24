enum NotificationActionType {
  accept,
  decline,
  custom,
}

class NotificationAction {
  final String id;
  final String text;
  final NotificationActionType type;
  final bool isDestructive;
  final bool requiresAuth;
  final String? icon; // Custom icon (Android: res name, iOS: SF Symbol)

  NotificationAction({
    required this.id,
    required this.text,
    required this.type,
    this.isDestructive = false,
    this.requiresAuth = false,
    this.icon,
  });

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'text': text,
      'type': type.name,
      'isDestructive': isDestructive,
      'requiresAuth': requiresAuth,
      'icon': icon,
    };
  }
}