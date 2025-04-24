<!--
This README describes the package. If you publish this package to pub.dev,
this README's contents appear on the landing page for your package.

For information about how to write a good package README, see the guide for
[writing package pages](https://dart.dev/guides/libraries/writing-package-pages).

For general information about developing packages, see the Dart guide for
[creating packages](https://dart.dev/guides/libraries/create-library-packages)
and the Flutter guide for
[developing packages and plugins](https://flutter.dev/developing-packages).
-->

# Flutter Incoming Request

A Flutter plugin for displaying incoming requests (like CallKit) with deep linking and custom UI. This plugin provides a unified way to handle incoming requests across both Android and iOS platforms, with native integration for better user experience.

## Features

- 🎯 **Cross-Platform Support**: Works seamlessly on both Android and iOS
- 📱 **Native Integration**: Uses CallKit on iOS and full-screen notifications on Android
- 🔗 **Deep Linking**: Supports deep linking to specific routes in your app
- 🎨 **Customizable UI**: Provides a default layout with the ability to create custom layouts
- 🔔 **Background Support**: Works even when the app is in the background
- 🔒 **Lock Screen Support**: Shows notifications on the lock screen
- ⚡ **Quick Actions**: Support for custom actions on notifications

## Getting Started

### Installation

Add the following to your `pubspec.yaml`:

```yaml
dependencies:
  flutter_incoming_request: ^1.0.0
```

### Platform Setup

#### Android

Add the following permissions to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
```

#### iOS

Add the following to your `Info.plist`:

```xml
<key>UIBackgroundModes</key>
<array>
    <string>voip</string>
    <string>remote-notification</string>
</array>
<key>NSMicrophoneUsageDescription</key>
<string>This app needs access to microphone for calls</string>
<key>NSCameraUsageDescription</key>
<string>This app needs access to camera for video calls</string>
```

## Usage

### Basic Usage

```dart
import 'package:flutter_incoming_request/flutter_incoming_request.dart';

// Create a request service instance
final requestService = RequestService();

// Show an incoming request
requestService.show(
  RequestData(
    id: "123",
    name: "John Doe",
    number: "+1234567890",
    type: NotificationType.call,
    routeName: "/call",
  ),
);

// Hide a request
requestService.hide("123");
```

### Custom Layout

```dart
class CustomLayoutBuilder implements CustomLayoutBuilder {
  @override
  Widget buildLayout(RequestData data, Function(NotificationAction) onAction) {
    return Material(
      child: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(colors: [Colors.blue, Colors.purple]),
        ),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            // Your custom layout here
          ],
        ),
      ),
    );
  }
}

// Use custom layout
requestService.show(
  RequestData(
    id: "123",
    name: "John Doe",
    routeName: "/call",
  ),
  builder: CustomLayoutBuilder(),
);
```

### Handling Actions

```dart
requestService.setActionHandler((String id, NotificationAction action) {
  // Handle the action
  print('Action ${action.id} pressed for request $id');
});

// Create a request with actions
requestService.show(
  RequestData(
    id: "123",
    name: "John Doe",
    actions: [
      NotificationAction(
        id: "accept",
        text: "Accept",
      ),
      NotificationAction(
        id: "reject",
        text: "Reject",
        isDestructive: true,
      ),
    ],
  ),
);
```

### Deep Linking

```dart
requestService.setOnRequestOpenedHandler((String? routeName, Map<String, dynamic>? args) {
  if (routeName != null) {
    Navigator.pushNamed(context, routeName, arguments: args);
  }
});
```

## API Reference

### RequestData

| Property         | Type                      | Description                                 |
| ---------------- | ------------------------- | ------------------------------------------- |
| id               | String                    | Unique identifier for the request           |
| name             | String                    | Name of the caller/requester                |
| avatar           | String?                   | URL of the avatar image                     |
| number           | String?                   | Phone number or identifier                  |
| type             | NotificationType          | Type of notification (call, message, other) |
| timeout          | Duration?                 | Auto-dismiss timeout                        |
| extra            | String?                   | Additional data                             |
| headers          | Map<String, dynamic>?     | Custom headers                              |
| actions          | List<NotificationAction>? | Custom actions                              |
| showInLockScreen | bool                      | Show in lock screen (default: true)         |
| showInApp        | bool                      | Show in app (default: true)                 |
| routeName        | String?                   | Route to navigate to                        |
| routeArgs        | Map<String, dynamic>?     | Route arguments                             |
| shouldStartApp   | bool                      | Force open app if closed (default: true)    |

### NotificationAction

| Property                 | Type    | Description                        |
| ------------------------ | ------- | ---------------------------------- |
| id                       | String  | Unique identifier for the action   |
| text                     | String  | Action text                        |
| icon                     | String? | Action icon                        |
| isDestructive            | bool    | Whether the action is destructive  |
| isAuthenticationRequired | bool    | Whether authentication is required |

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

If you find this package useful, please consider supporting it by giving it a ⭐️ on GitHub.
