import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'gallery_method_channel.dart';

abstract class GalleryPlatform extends PlatformInterface {
  /// Constructs a GalleryPlatform.
  GalleryPlatform() : super(token: _token);

  static final Object _token = Object();

  static GalleryPlatform _instance = MethodChannelGallery();

  /// The default instance of [GalleryPlatform] to use.
  ///
  /// Defaults to [MethodChannelGallery].
  static GalleryPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [GalleryPlatform] when
  /// they register themselves.
  static set instance(GalleryPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }



  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<String?> openGallery() {
    throw UnimplementedError('openGallery() has not been implemented.');
  }
}
