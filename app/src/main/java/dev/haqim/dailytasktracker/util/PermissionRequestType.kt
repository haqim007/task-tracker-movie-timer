package dev.haqim.dailytasktracker.util

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

typealias ManifestPermissionName = String

/**
 * Permission request type
 *
 * @property value
 * @constructor Create empty Permission request type
 */
enum class PermissionRequestType(val value: ManifestPermissionName?){
    /**
     * Camera Req
     *
     * @constructor Create empty Camera Req
     */
    CAMERA_REQ(Manifest.permission.CAMERA),

    /**
     * Ex Storage Req
     *
     * @constructor Create empty Ex Storage Req
     */
    EX_STORAGE_REQ(
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_EXTERNAL_STORAGE
        else
            null
    ),

    /**
     * Ex Write Storage Req
     *
     * @constructor Create empty Ex Write Storage Req
     */
    EX_WRITE_STORAGE_REQ(Manifest.permission.WRITE_EXTERNAL_STORAGE),

    /**
     * Read Images
     *
     * @constructor Create empty Read Images
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    READ_IMAGES(Manifest.permission.READ_MEDIA_IMAGES),

    /**
     * Read Video
     *
     * @constructor Create empty Read Video
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    READ_VIDEO(Manifest.permission.READ_MEDIA_VIDEO),

    /**
     * Post ]Notification
     *
     * @constructor Create empty Post Notification
     */
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    POST_NOTIFICATION(Manifest.permission.POST_NOTIFICATIONS)
}

/**
 * Permission request type handler
 *
 * @constructor Create empty Permission request type handler
 */
abstract class PermissionRequestTypeHandler(){
    /**
     * Callback for camera request
     *
     */
    open fun onCameraRequest() {}

    /**
     * Callback for write external storage request
     *
     */
    open fun onWriteExternalStorageRequest() {}

    /**
     * Callback for read images request
     *
     */
    open fun onReadImagesRequest() {}

    /**
     * Callback for read videos request
     *
     */
    open fun onReadVideosRequest() {}

    /**
     * Callback for read external storage request
     *
     */
    open fun onReadExternalStorageRequest() {}

    /**
     * Callback for post notification request
     *
     */
    open fun onPostNotificationRequest() {}
}

/**
 * Handle [PermissionRequestType] condition
 *
 * @param handler
 */
fun PermissionRequestType.handle(handler: PermissionRequestTypeHandler){
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
        when(this) {
            PermissionRequestType.CAMERA_REQ -> {
                handler.onCameraRequest()
            }
            PermissionRequestType.EX_WRITE_STORAGE_REQ -> {
                handler.onWriteExternalStorageRequest()
            }
            PermissionRequestType.READ_IMAGES ->
                handler.onReadImagesRequest()
            PermissionRequestType.READ_VIDEO ->
                handler.onReadVideosRequest()

            PermissionRequestType.POST_NOTIFICATION ->
                handler.onPostNotificationRequest()
            else -> {}
        }
    }else{
        when(this) {
            PermissionRequestType.CAMERA_REQ -> {
                handler.onCameraRequest()
            }
            PermissionRequestType.EX_WRITE_STORAGE_REQ -> {
                handler.onWriteExternalStorageRequest()
            }
            PermissionRequestType.EX_STORAGE_REQ -> {
                handler.onReadExternalStorageRequest()
            }
            else -> {}
        }

    }
}