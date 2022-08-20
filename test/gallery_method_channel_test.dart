import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:gallery/gallery_method_channel.dart';

void main() {
  MethodChannelGallery platform = MethodChannelGallery();
  const MethodChannel channel = MethodChannel('gallery');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
