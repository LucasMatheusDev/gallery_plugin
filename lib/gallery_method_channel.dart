import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'gallery_platform_interface.dart';

/// An implementation of [GalleryPlatform] that uses method channels.
class MethodChannelGallery extends GalleryPlatform with FlutterPlugin {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('gallery');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<String?> openGallery() async {
    final image = await methodChannel_invokeMethod<String>("openGallery");
    return image?.path;
  }
}