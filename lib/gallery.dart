import 'package:permission_handler/permission_handler.dart';

import 'gallery_platform_interface.dart';

class Gallery {
  Future<String?> getPlatformVersion() {
    return GalleryPlatform.instance.getPlatformVersion();
  }

  Future<String?> openGallery() async {
    if (await requestPermission()) {
      return await GalleryPlatform.instance.openGallery();
    } else {
      throw Exception('Permission denied');
    }
  }

  Future<bool> hasPermission() async {
    final bool hasPermissionForOpenGallery = await Permission.storage.isGranted;
    return hasPermissionForOpenGallery;
  }

  Future<bool> requestPermission() async {
    if (await hasPermission()) {
      return true;
    } else {
      final result = await Permission.storage.request();
      return result.isGranted;
    }
  }
}
