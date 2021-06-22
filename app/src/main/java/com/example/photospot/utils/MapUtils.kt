package com.example.photospot.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

object MapUtils {

    fun checkPermissionsGranted(context: Context): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    fun requestPermissions(context: Context?, permission: String?) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        //TODO update documetntation
        ActivityCompat.shouldShowRequestPermissionRationale((context as Activity?)!!, "")
    }

    @JvmStatic
    @Throws(PackageManager.NameNotFoundException::class)
    fun HandlePermissions(context: Context) {
        if (!checkPermissionsGranted(context)) {
            return
        }

        // Get permisssions listed in the manifest
        val requestedPermissions = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_PERMISSIONS).requestedPermissions
        for (permission in requestedPermissions) {
            if (!isPermissionGranted(context, permission)) {
                requestPermissions(context, permission)
            }
        }
    }

    fun isPermissionGranted(context: Context?, permission: String?): Boolean {
        return ActivityCompat.checkSelfPermission(context!!, permission!!) == PackageManager.PERMISSION_GRANTED
    }
}