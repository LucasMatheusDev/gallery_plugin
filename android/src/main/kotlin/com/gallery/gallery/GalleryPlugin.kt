package com.gallery.gallery

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry
import java.io.File


/** GalleryPlugin */
class GalleryPlugin: FlutterPlugin, MethodCallHandler , ActivityAware, PluginRegistry.ActivityResultListener{
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity


  private lateinit var channel: MethodChannel

  private val codeImage = 22

  private val fileUtils: File? = null

  private var pluginBinding: FlutterPluginBinding? = null

  private var activity: Activity? = null

  private  var resultGlobal: Result? = null

  private  var prefs : SharedPreferences? = null


  var uri_imagem: Uri? = null

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "gallery")
    channel.setMethodCallHandler(this)
    pluginBinding = flutterPluginBinding
  }


  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    resultGlobal = result
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else if (call.method == "openGallery") {
      getImageGallery()
    } else {
      result.notImplemented()
    }
  }


  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    pluginBinding = null

  }


  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
    binding.addActivityResultListener(this)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity();
  }


  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    onAttachedToActivity(binding);

  }

  override fun onDetachedFromActivity() {
    activity = null
  }


  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    return if (requestCode == codeImage) {
      handleChooseImageResult(resultCode, data)
      true;
    } else{
      false;
    }
  }


  private fun handleChooseImageResult(resultCode: Int, data: Intent?)  {
    if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
      val path = getRealPathFromUri(activity!!.baseContext,data.data)
      resultGlobal!!.success(path)
    } else {
      resultGlobal!!.success(null)
    }
  }

  private fun getImageGallery() {
    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    activity!!.startActivityForResult(intent, codeImage)
    val contentValues = ContentValues()
    contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/")
    uri_imagem = activity!!.contentResolver.insert(
      MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
      contentValues
    )
    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri_imagem)
  }

 private fun getRealPathFromUri(context: Context, contentUri: Uri?): String? {
    var cursor: Cursor? = null
    return try {
      val proj = arrayOf(MediaStore.Images.Media.DATA)
      cursor = context.getContentResolver().query(contentUri!!, proj, null, null, null)
      val column_index: Int = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
      cursor!!.moveToFirst()
      cursor!!.getString(column_index)

    return  "";
    } finally {
      if (cursor != null) {
        cursor.close()
      }
    }
  }

  
}
