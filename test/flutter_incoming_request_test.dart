import 'package:flutter_incoming_request/flutter_incoming_request.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  group('RequestData Tests', () {
    test('RequestData toMap should include all fields', () {
      final request = RequestData(
        id: 'test-id',
        name: 'Test User',
        avatar: 'https://example.com/avatar.jpg',
        number: '+1234567890',
        type: NotificationType.call,
        timeout: const Duration(seconds: 30),
        extra: 'test extra',
        headers: {'key': 'value'},
        actions: [
          NotificationAction(
            id: 'action1',
            text: 'Accept',
          ),
        ],
        showInLockScreen: true,
        showInApp: true,
        routeName: '/test',
        routeArgs: {'arg1': 'value1'},
        shouldStartApp: true,
      );

      final map = request.toMap();
      expect(map['id'], 'test-id');
      expect(map['name'], 'Test User');
      expect(map['avatar'], 'https://example.com/avatar.jpg');
      expect(map['number'], '+1234567890');
      expect(map['type'], 'call');
      expect(map['timeout'], 30000);
      expect(map['extra'], 'test extra');
      expect(map['headers'], {'key': 'value'});
      expect(map['showInLockScreen'], true);
      expect(map['showInApp'], true);
      expect(map['routeName'], '/test');
      expect(map['routeArgs'], {'arg1': 'value1'});
      expect(map['shouldStartApp'], true);
    });
  });
}
