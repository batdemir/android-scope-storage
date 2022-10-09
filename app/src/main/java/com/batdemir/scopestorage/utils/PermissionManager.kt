package com.batdemir.scopestorage.utils

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.batdemir.scopestorage.core.BaseActivity

object PermissionManager {
    var readPermission: Boolean = false
    var writePermission: Boolean = false
    private fun getPermissionLauncher(activity: BaseActivity): ActivityResultLauncher<Array<String>> {
        return activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            readPermission =
                it[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermission
            writePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                it[Manifest.permission.ACCESS_MEDIA_LOCATION]
            } else {
                it[Manifest.permission.WRITE_EXTERNAL_STORAGE]
            } ?: writePermission
        }
    }

    fun permissions(activity: BaseActivity) {
        val hasReadPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) ==
            PackageManager.PERMISSION_GRANTED
        val hasWritePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_MEDIA_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED
        }
        readPermission = hasReadPermission
        writePermission = hasWritePermission
        val permissionsToRequest = mutableListOf<String>()
        if (!readPermission) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (!writePermission) {
            permissionsToRequest.add(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    Manifest.permission.ACCESS_MEDIA_LOCATION
                } else {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                }
            )
        }
        if (permissionsToRequest.isNotEmpty()) {
            getPermissionLauncher(activity).launch(permissionsToRequest.toTypedArray())
        }
    }
}
