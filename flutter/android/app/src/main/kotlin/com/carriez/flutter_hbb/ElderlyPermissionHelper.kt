package com.carriez.flutter_hbb

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationManagerCompat
import com.hjq.permissions.XXPermissions
import com.hjq.permissions.Permission
import com.hjq.permissions.OnPermissionCallback
import io.flutter.embedding.android.FlutterActivity
import kotlin.math.log

class ElderlyPermissionHelper(private val activity: Activity) {
    
    companion object {
        const val TAG = "ElderlyPermissionHelper"
        const val REQUEST_PERMISSION_CODE = 1001
        const val REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = 1002
    }
    
    interface PermissionCallback {
        fun onAllPermissionsGranted()
        fun onPermissionsDenied(permissions: List<String>)
    }
    
    fun requestAllEssentialPermissions(callback: PermissionCallback) {
        val permissions = mutableListOf<String>()
        
        // Storage permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Permission.READ_MEDIA_IMAGES)
            permissions.add(Permission.READ_MEDIA_VIDEO)
            permissions.add(Permission.READ_MEDIA_AUDIO)
        } else {
            permissions.add(Permission.READ_EXTERNAL_STORAGE)
            permissions.add(Permission.WRITE_EXTERNAL_STORAGE)
        }
        
        // Record audio for remote connection
        permissions.add(Permission.RECORD_AUDIO)
        
        // Camera for QR code scanning
        permissions.add(Permission.CAMERA)
        
        // Overlay for remote control
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissions.add(Permission.SYSTEM_ALERT_WINDOW)
        }
        
        // Notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Permission.POST_NOTIFICATIONS)
        }
        
        XXPermissions.with(activity)
            .permission(permissions)
            .request(object : OnPermissionCallback {
                override fun onGranted(permissions: MutableList<String>, all: Boolean) {
                    if (all) {
                        requestIgnoreBatteryOptimizations()
                        callback.onAllPermissionsGranted()
                    } else {
                        callback.onPermissionsDenied(permissions)
                    }
                }
                
                override fun onDenied(permissions: MutableList<String>, never: Boolean) {
                    callback.onPermissionsDenied(permissions)
                }
            })
    }
    
    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestIgnoreBatteryOptimizations() {
        val powerManager = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!powerManager.isIgnoringBatteryOptimizations(activity.packageName)) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${activity.packageName}")
            }
            activity.startActivityForResult(intent, REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
        }
    }
    
    @RequiresApi(Build.VERSION_CODES.M)
    fun checkOverlayPermission(): Boolean {
        return Settings.canDrawOverlays(activity)
    }
    
    @RequiresApi(Build.VERSION_CODES.M)
    fun requestOverlayPermission() {
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
            data = Uri.parse("package:${activity.packageName}")
        }
        activity.startActivity(intent)
    }
    
    fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${activity.packageName}")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        activity.startActivity(intent)
    }
    
    fun checkAreAllPermissionsGranted(): Boolean {
        val missingPermissions = mutableListOf<String>()
        
        // Storage permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES) != 
                android.content.pm.PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != 
                android.content.pm.PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != 
                android.content.pm.PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        
        // Audio
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != 
            android.content.pm.PackageManager.PERMISSION_GRANTED) {
            missingPermissions.add(Manifest.permission.RECORD_AUDIO)
        }
        
        // Camera
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != 
            android.content.pm.PackageManager.PERMISSION_GRANTED) {
            missingPermissions.add(Manifest.permission.CAMERA)
        }
        
        // Overlay
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(activity)) {
                missingPermissions.add("android.permission.SYSTEM_ALERT_WINDOW")
            }
        }
        
        // Notifications
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = NotificationManagerCompat.from(activity)
            if (!notificationManager.areNotificationsEnabled()) {
                missingPermissions.add("android.permission POST_NOTIFICATIONS")
            }
        }
        
        return missingPermissions.isEmpty()
    }
}