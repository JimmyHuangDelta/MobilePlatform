package com.delta.mobileplatform.web.controller.messageQueue

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.delta.mobileplatform.activity.WebActivity
import com.delta.mobileplatform.R
import com.delta.mobileplatform.web.controller.permission.notifyPermission
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject
import kotlin.random.Random

class Notify @Inject constructor(
    @ActivityContext private val context: Context
) {

    init {
        // Call createNotificationChannel when the com.delta.mobileplatform.domain.messageQueue.Notify instance is created.
        createNotificationChannel()
    }

    private fun builder(
        textTitle: String,
        textContent: String,
        group: String,
        data: String? = null
    ): NotificationCompat.Builder {
        val intent = Intent(context, WebActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            data?.let { putExtra(MESSAGE_DATA, it) }
            // Add the LINK extra to the intent
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(textTitle)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentText(textContent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle())
            .setGroup(group)
            .setSilent(true)
            .setVibrate(longArrayOf(0))
            .setAutoCancel(true) // Auto dismiss the notification when clicked
    }

    fun showNotify(title: String, content: String, group: String, data: String?) {
        if (Build.VERSION.SDK_INT >= 33 && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Handle the case where the permission is not granted.
            // You can request the permission here if needed.
            (context as WebActivity).requestPermissions(notifyPermission, 10010)
            return
        }

        // Generate a unique notification ID (use your logic here)
        val notificationId = generateUniqueNotificationId()

        with(NotificationManagerCompat.from(context)) {
            notify(notificationId, builder(title, content, group, data).build())
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "delta_internal"
        const val MESSAGE_DATA = "message_data"
    }

    // Implement a method to generate a unique notification ID
    private fun generateUniqueNotificationId(): Int {
        // Implement your logic to generate a unique ID, e.g., using a counter.
        return Random.nextInt()
    }
}
