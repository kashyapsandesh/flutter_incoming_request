import 'package:flutter/material.dart';
import 'package:flutter_incoming_request/flutter_incoming_request.dart';

abstract class CustomLayoutBuilder {
  Widget buildLayout(RequestData data, Function(NotificationAction) onAction);
}

class DefaultLayoutBuilder implements CustomLayoutBuilder {
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
            if (data.avatar != null)
              CircleAvatar(
                backgroundImage: NetworkImage(data.avatar!),
                radius: 40,
              ),
            const SizedBox(height: 16),
            Text(
              data.name,
              style: const TextStyle(
                fontSize: 20,
                fontWeight: FontWeight.bold,
                color: Colors.white,
              ),
            ),
            if (data.number != null) ...[
              const SizedBox(height: 8),
              Text(
                data.number!,
                style: const TextStyle(
                  fontSize: 16,
                  color: Colors.white70,
                ),
              ),
            ],
            const SizedBox(height: 24),
            if (data.actions != null && data.actions!.isNotEmpty)
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: data.actions!.map((action) {
                  return Padding(
                    padding: const EdgeInsets.symmetric(horizontal: 8),
                    child: ElevatedButton(
                      onPressed: () => onAction(action),
                      child: Text(action.text),
                    ),
                  );
                }).toList(),
              ),
          ],
        ),
      ),
    );
  }
}
