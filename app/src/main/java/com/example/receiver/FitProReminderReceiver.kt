package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity

class FitProReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderType = intent.getStringExtra(EXTRA_REMINDER_TYPE) ?: "workout"
        val title = intent.getStringExtra(EXTRA_TITLE) ?: getNotificationTitle(reminderType)
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: getNotificationMessage(reminderType)

        showNotification(context, reminderType, title, message)
    }

    private fun showNotification(context: Context, reminderType: String, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create Channel if Android O+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "FitPro Daily Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily scheduled reminders for workouts and meal logging."
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            reminderType.hashCode(),
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationId = when (reminderType) {
            "workout" -> 1001
            "meal" -> 1002
            else -> 1000
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    private fun getNotificationTitle(reminderType: String): String {
        return when (reminderType) {
            "workout" -> "🏋️ Time for Your Daily Workout!"
            "meal" -> "🥗 Log Your Meals & Water Intake!"
            else -> "💪 FitPro Reminder"
        }
    }

    private fun getNotificationMessage(reminderType: String): String {
        return when (reminderType) {
            "workout" -> "Consistency is key! Tap to view today's scheduled AI workout routine and keep your streak alive."
            "meal" -> "Stay on track with your macros! Log your calories, protein, and water for optimal recovery."
            else -> "Keep pushing towards your fitness goals today!"
        }
    }

    companion object {
        const val CHANNEL_ID = "fitpro_daily_reminders_channel"
        const val EXTRA_REMINDER_TYPE = "extra_reminder_type"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
        const val ACTION_REMINDER = "com.example.ACTION_FITPRO_REMINDER"
    }
}
