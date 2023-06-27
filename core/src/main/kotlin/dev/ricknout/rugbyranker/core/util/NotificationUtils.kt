package dev.ricknout.rugbyranker.core.util

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

object NotificationUtils {

    @SuppressLint("InlinedApi")
    fun areNotificationsEnabled(context: Context) = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.POST_NOTIFICATIONS,
    ) == PackageManager.PERMISSION_GRANTED

    @TargetApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(
        notificationManager: NotificationManagerCompat,
        id: String,
        name: String,
        importance: Int = NotificationManagerCompat.IMPORTANCE_DEFAULT,
    ): NotificationChannelCompat {
        return NotificationChannelCompat.Builder(id, importance)
            .setName(name)
            .build()
            .also { channel ->
                notificationManager.createNotificationChannel(channel)
            }
    }

    fun isNotificationChannelEnabled(
        notificationManager: NotificationManagerCompat,
        channelId: String,
    ): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // Notification channels are only applicable on SDK 26+
            return true
        }
        val channel = notificationManager.getNotificationChannelCompat(channelId)
        // Return true if channel doesn't exist yet or importance isn't NONE
        return channel == null || channel.importance != NotificationManagerCompat.IMPORTANCE_NONE
    }

    fun showNotificationSettings(context: Context) {
        val intent = Intent().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            } else {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", context.packageName, null)
                data = uri
            }
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    const val NOTIFICATION_CHANNEL_ID_LIVE = "live_notification_channel"
    const val NOTIFICATION_CHANNEL_ID_RESULT = "result_notification_channel"
}
