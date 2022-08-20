// ignore: non_constant_identifier_names
import 'package:image_picker/image_picker.dart' as flutter_method;

mixin FlutterPlugin {
  // ignore: non_constant_identifier_names
  methodChannel_invokeMethod<T>(String method) =>
      // ignore: invalid_use_of_visible_for_testing_member
      flutter_method.ImagePicker.platform
          .pickImage(source: flutter_method.ImageSource.gallery);
}
