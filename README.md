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
- 🔄 **Event-based Communication**: Real-time updates between native and Flutter code
- 🛣️ **Custom Routing**: Support for navigation with arguments

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
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

#### iOS

1. Add the following capabilities to your Xcode project:

   - Background Modes
     - Voice over IP
     - Remote notifications
   - Push Notifications

2. Add the following to your `Info.plist`:

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

3. Create a new Swift file `FlutterIncomingRequestPlugin.swift` in your iOS project:

```swift
import Flutter
import UIKit
import CallKit
import PushKit

public class FlutterIncomingRequestPlugin: NSObject, FlutterPlugin, PKPushRegistryDelegate {
    private var eventSink: FlutterEventSink?
    private let callController = CXCallController()
    private var voipRegistry: PKPushRegistry?

    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "flutter_incoming_request", binaryMessenger: registrar.messenger())
        let instance = FlutterIncomingRequestPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)

        // Set up event channel
        let eventChannel = FlutterEventChannel(name: "flutter_incoming_request/events", binaryMessenger: registrar.messenger())
        eventChannel.setStreamHandler(instance)

        // Initialize PushKit
        instance.voipRegistry = PKPushRegistry(queue: .main)
        instance.voipRegistry?.delegate = instance
        instance.voipRegistry?.desiredPushTypes = [.voIP]
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        switch call.method {
        case "show":
            if let args = call.arguments as? [String: Any] {
                showRequest(args)
            }
            result(nil)
        case "hide":
            if let args = call.arguments as? [String: Any],
               let id = args["id"] as? String {
                hideRequest(id)
            }
            result(nil)
        default:
            result(FlutterMethodNotImplemented)
        }
    }

    private func showRequest(_ data: [String: Any]) {
        let name = data["name"] as? String ?? ""
        let number = data["number"] as? String
        let routeName = data["routeName"] as? String
        let routeArgs = data["routeArgs"] as? [String: Any]

        // Create a local notification
        let content = UNMutableNotificationContent()
        content.title = "Incoming Request"
        content.body = number != nil ? "\(name) (\(number!))" : name
        content.sound = .default

        // Add route information to userInfo
        var userInfo: [String: Any] = [:]
        if let routeName = routeName {
            userInfo["routeName"] = routeName
        }
        if let routeArgs = routeArgs {
            userInfo["routeArgs"] = routeArgs
        }
        content.userInfo = userInfo

        // Create notification request
        let request = UNNotificationRequest(
            identifier: UUID().uuidString,
            content: content,
            trigger: nil
        )

        // Add notification
        UNUserNotificationCenter.current().add(request)

        // Send event to Flutter
        sendEvent("request", data: [
            "name": name,
            "number": number ?? "",
            "routeName": routeName ?? "",
            "routeArgs": routeArgs ?? [:]
        ])
    }

    private func hideRequest(_ id: String) {
        UNUserNotificationCenter.current().removeDeliveredNotifications(withIdentifiers: [id])
    }

    private func sendEvent(_ event: String, data: [String: Any]) {
        eventSink?([
            "event": event,
            "data": data
        ])
    }

    // MARK: - PKPushRegistryDelegate

    public func pushRegistry(_ registry: PKPushRegistry, didUpdate pushCredentials: PKPushCredentials, for type: PKPushType) {
        // Handle updated push credentials
    }

    public func pushRegistry(_ registry: PKPushRegistry, didReceiveIncomingPushWith payload: PKPushPayload, for type: PKPushType, completion: @escaping () -> Void) {
        // Handle incoming push notification
        if let data = payload.dictionaryPayload as? [String: Any] {
            showRequest(data)
        }
        completion()
    }
}

// MARK: - FlutterStreamHandler

extension FlutterIncomingRequestPlugin: FlutterStreamHandler {
    public func onListen(withArguments arguments: Any?, eventSink events: @escaping FlutterEventSink) -> FlutterError? {
        eventSink = events
        return nil
    }

    public func onCancel(withArguments arguments: Any?) -> FlutterError? {
        eventSink = nil
        return nil
    }
}
```

4. Update your `AppDelegate.swift`:

```swift
import UIKit
import Flutter
import flutter_incoming_request

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate {
    override func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
    ) -> Bool {
        // Register for remote notifications
        UNUserNotificationCenter.current().delegate = self
        application.registerForRemoteNotifications()

        // Handle initial route from launch options
        if let userInfo = launchOptions?[.remoteNotification] as? [String: Any] {
            handleNotification(userInfo)
        }

        return super.application(application, didFinishLaunchingWithOptions: launchOptions)
    }

    override func application(_ application: UIApplication,
                            didReceiveRemoteNotification userInfo: [AnyHashable : Any],
                            fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {
        handleNotification(userInfo)
        completionHandler(.newData)
    }

    private func handleNotification(_ userInfo: [AnyHashable: Any]) {
        if let routeName = userInfo["routeName"] as? String,
           let routeArgs = userInfo["routeArgs"] as? [String: Any] {
            // Handle navigation to route
            // You can use a method channel to communicate with Flutter
        }
    }
}
```

## Usage

### 1. Initialize the Request Handler

Initialize the request handler in your app's main.dart or wherever you want to handle incoming requests:

```dart
void main() {
  RequestHandler.initialize(
    onRequest: (eventType, data) {
      // Handle the incoming request
      final name = data['name'] as String;
      final number = data['number'] as String?;
      final routeName = data['routeName'] as String?;
      final routeArgs = data['routeArgs'] as Map<String, dynamic>?;
      final shouldStartApp = data['shouldStartApp'] as bool;

      // Do something with the request data
      print('Received request from $name');

      // Navigate to a specific route if needed
      if (routeName != null) {
        // Use your navigation method here
        // e.g., Navigator.pushNamed(context, routeName, arguments: routeArgs);
      }
    },
  );

  runApp(MyApp());
}
```

### 2. Show a Request

```dart
final requestService = RequestService();

// Show a request
await requestService.show(RequestData(
  name: 'John Doe',
  number: '+1234567890',
  routeName: '/request_details',
  routeArgs: {'requestId': '123'},
  shouldStartApp: true,
));
```

### 3. Hide a Request

```dart
await requestService.hide('request_id');
```

### 4. Clean Up

Don't forget to dispose of the request handler when your app is closing:

```dart
@override
void dispose() {
  RequestHandler.dispose();
  super.dispose();
}
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

## Event Handling

The package uses an event-based system to communicate between native and Flutter code. Events are sent with the following structure:

```dart
{
  'event': 'request',
  'data': {
    'name': String,
    'number': String?,
    'routeName': String?,
    'routeArgs': Map<String, dynamic>?,
    'shouldStartApp': bool
  }
}
```

## Platform-Specific Notes

### Android

- Requires Android API level 21 or higher
- Uses a foreground service for reliable notification delivery
- Supports custom notification layouts
- Handles app state transitions gracefully

### iOS

- Uses standard iOS notification system
- Supports basic notification customization
- Handles app state transitions according to iOS guidelines

## iOS-Specific Features

### CallKit Integration

The iOS implementation uses CallKit to provide a native calling experience:

1. Incoming calls show the native call screen
2. Calls can be answered from the lock screen
3. Calls are integrated with the system's call history
4. Supports call waiting and call merging

### PushKit Integration

For reliable VoIP notifications:

1. Register for VoIP push notifications
2. Handle incoming calls even when the app is terminated
3. Maintain a persistent connection for real-time updates

### Background Modes

The iOS implementation supports:

1. Background fetch for periodic updates
2. VoIP for real-time communication
3. Remote notifications for push messages
4. Background processing for call handling

### Notification Handling

iOS notifications are handled through:

1. UNUserNotificationCenter for local notifications
2. PushKit for VoIP notifications
3. CallKit for call-related notifications
4. Background fetch for periodic updates

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

If you find this package useful, please consider supporting it by giving it a ⭐️ on GitHub.
