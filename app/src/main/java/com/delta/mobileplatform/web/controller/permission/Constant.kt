package com.delta.mobileplatform.web.controller.permission

import android.Manifest
import android.os.Build

val blePermission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
val notifyPermission =
    if (Build.VERSION.SDK_INT >= 33) arrayOf(Manifest.permission.POST_NOTIFICATIONS) else arrayOf()
val foregroundService = if (Build.VERSION.SDK_INT >= 28) arrayOf(
    Manifest.permission.FOREGROUND_SERVICE
) else arrayOf()


val BLUETOOTH_PERMISSIONS =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADMIN
        )
    } else {
        arrayOf(
            Manifest.permission.BLUETOOTH,
        )
    }

val MEDIA_PERMISSIONS_33 =
    if (Build.VERSION.SDK_INT >= 33) arrayOf(Manifest.permission.READ_MEDIA_IMAGES) else arrayOf()

val MEDIA_PERMISSIONS =
    arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    ) + MEDIA_PERMISSIONS_33

val allPermission =
    blePermission + notifyPermission + foregroundService + BLUETOOTH_PERMISSIONS + MEDIA_PERMISSIONS
