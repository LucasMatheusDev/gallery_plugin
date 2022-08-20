// ignore: non_constant_identifier_names
import 'package:flutter_test/flutter_test.dart';
import 'package:gallery/gallery.dart';
import 'package:gallery/gallery_method_channel.dart';
import 'package:gallery/gallery_platform_interface.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockGalleryPlatform
    with MockPlatformInterfaceMixin
    implements GalleryPlatform {
  @override
  Future<String?> getPlatformVersion() => Future.value('42');

  @override
  Future<String?> openGallery() {
    return Future.value(
      '/storage/emulated/0/DCIM/Camera/IMG_20190901_095139.jpg',
    );
  }
}

void main() {
  final GalleryPlatform initialPlatform = GalleryPlatform.instance;

  test('$MethodChannelGallery is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelGallery>());
  });

  test('getPlatformVersion', () async {
    Gallery galleryPlugin = Gallery();
    MockGalleryPlatform fakePlatform = MockGalleryPlatform();
    GalleryPlatform.instance = fakePlatform;

    expect(await galleryPlugin.getPlatformVersion(), '42');
  });
}
