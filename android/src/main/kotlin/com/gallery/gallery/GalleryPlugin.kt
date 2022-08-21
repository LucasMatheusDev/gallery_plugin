package com.gallery.gallery

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry


/** GalleryPlugin */
@Suppress("DEPRECATION")
class GalleryPlugin: FlutterPlugin, MethodCallHandler , ActivityAware, PluginRegistry.ActivityResultListener {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity


  private lateinit var channel: MethodChannel

  private val codeImage = 22


  private var pluginBinding: FlutterPluginBinding? = null

  private var activity: Activity? = null

  private var resultGlobal: Result? = null

  private var prefs: SharedPreferences? = null

  private val sharedPreferenceName = "flutter_gallery_shared_preference"


  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "gallery")
    channel.setMethodCallHandler(this)
    pluginBinding = flutterPluginBinding
  }


  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    resultGlobal = result
    when (call.method) {
        "getPlatformVersion" -> {
          result.success("Android ${Build.VERSION.RELEASE}")
        }
        "openGallery" -> {
          openGalleryAndSelectImage()
        }
        else -> {
          result.notImplemented()
        }
    }
  }


  override fun onDetachedFromEngine(@NonNull binding: FlutterPluginBinding) {
    pluginBinding = null

  }

  private fun initCache(context: Context) {
    prefs = context.getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity
    binding.addActivityResultListener(this)
    initCache(activity!!.baseContext)
  }

  override fun onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity()
  }


  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    onAttachedToActivity(binding)

  }

  override fun onDetachedFromActivity() {
    activity = null
  }


  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
    return if (requestCode == codeImage) {
      handleChooseImageResult(resultCode, data)
      true
    } else {
      false
    }
  }


  private fun handleChooseImageResult(resultCode: Int, data: Intent?) {
    if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
      val uri : Uri = Uri.parse(data.data.toString())
      val path = getRealPathFromURI(uri)
      saveResult(path.toString())
    } else {
      resultGlobal!!.success(null)
    }
  }

  
  private fun openGalleryAndSelectImage(){
    val intent = Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
    intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
      intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
      intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
    }


    intent.resolveActivity(activity!!.packageManager)
    activity!!.startActivityForResult(intent, codeImage)
  }

  private fun getRealPathFromURI(contentURI: Uri) : String? {
    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = activity!!.contentResolver.query(contentURI, filePathColumn, null, null, null)
    cursor!!.moveToFirst()  
    val columnIndex = cursor.getColumnIndex(filePathColumn[0])      
    val picturePath = cursor.getString(columnIndex)
    cursor.close()
    return picturePath
  }
  private fun saveResult(path: String) {
    resultGlobal!!.success(path)
  }
}
