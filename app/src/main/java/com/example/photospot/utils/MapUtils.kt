package com.example.photospot.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat.checkSelfPermission
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest


object MapUtils {
    @JvmStatic
    @Throws(PackageManager.NameNotFoundException::class)
    fun checkPermission(
        context: Context,
        requestPermissionLauncher: ActivityResultLauncher<String>
    ) {
        val permissions = context.packageManager.getPackageInfo(
            context.packageName,
            PackageManager.GET_PERMISSIONS
        ).requestedPermissions

        for (permission in permissions) {
            if (checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    fun checkPermissionEnabled(context: Context, permission: Array<String>): Boolean {
        permission.forEach { p ->
            if (checkSelfPermission(context, p) == PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }
        }
        return false
    }

    fun autocompleteRequestBuilder(
        token: AutocompleteSessionToken,
        query: String
    ): FindAutocompletePredictionsRequest {
        return FindAutocompletePredictionsRequest.builder()
            .setSessionToken(token)
            .setQuery(query)
            .build()
    }
}
