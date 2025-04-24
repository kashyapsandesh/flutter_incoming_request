import 'package:flutter/material.dart';
import 'package:flutter_incoming_request/flutter_incoming_request.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: Center(
          child: ElevatedButton(
            onPressed: () {
              final requestService = RequestService();
              requestService.show(
                RequestData(
                  id: "123",
                  name: "John Doe",
                  routeName: "/call",
                ),
              );
            },
            child: const Text("Show Request"),
          ),
        ),
      ),
    );
  }
}
