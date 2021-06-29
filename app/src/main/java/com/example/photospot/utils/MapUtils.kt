package com.example.photospot.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat


object MapUtils {

    fun requestPermissions(context: Context?, Permission: String?) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        //TODO update documentation
        Log.e("SC", Permission.toString())
        if (Permission != null) {
            ActivityCompat.shouldShowRequestPermissionRationale(((context as Activity?)!!), Permission)
        }
    }

    @JvmStatic
    @Throws(PackageManager.NameNotFoundException::class)
    fun checkPermissions(context: Context) {
        // Get permisssions listed in the manifest
        val permissions = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_PERMISSIONS
        ).requestedPermissions

        for (permission in permissions) {
            Log.e("permission", permission.toString())
            Log.e("permission", isPermissionGranted(context, permission).toString())
            if (!isPermissionGranted(context, permission)) {
                requestPermissions(context, permission)
            }
        }
    }

    fun isPermissionGranted(context: Context?, permission: String?): Boolean {
        return ActivityCompat.checkSelfPermission(
            context!!,
            permission!!
        ) == PackageManager.PERMISSION_GRANTED
    }
}