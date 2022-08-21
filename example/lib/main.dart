import 'dart:async';
import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:gallery/gallery.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  final _galleryPlugin = Gallery();
  final ValueNotifier<String?> _valueNotifier = ValueNotifier<String?>(null);

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion = await _galleryPlugin.getPlatformVersion() ??
          'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin Gallery example'),
        ),
        body: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Text('Image Selected'),
              ValueListenableBuilder<String?>(
                valueListenable: _valueNotifier,
                builder: (context, value, child) {
                  return value == null
                      ? const SizedBox()
                      : SizedBox(
                          height: 300,
                          width: 300,
                          child: Image(
                            image: FileImage(File(value)),
                            fit: BoxFit.cover,
                            errorBuilder: (context, error, stackTrace) {
                              debugPrint("ERROR $error");
                              return Text(error.toString());
                            },
                          ),
                        );
                },
              ),
            ],
          ),
        ),
        floatingActionButton: FloatingActionButton(
          onPressed: () async {
            try {
              _valueNotifier.value = await _galleryPlugin.openGallery();
            } on PlatformException catch (e) {
              ScaffoldMessenger.of(context).showSnackBar(
                SnackBar(
                  content: Text(
                    e.message ??
                        "Erro ao abrir galeria, verifique as permissões"
                            " e tente novamente",
                  ),
                ),
              );
              debugPrint("ERROR ${e.message}");
            }
          },
          child: const Icon(Icons.photo_library),
        ),
      ),
    );
  }
}
