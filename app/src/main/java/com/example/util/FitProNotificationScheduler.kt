package com.example.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.receiver.FitProReminderReceiver
import java.util.Calendar

object FitProNotificationScheduler {

    fun scheduleDailyReminder(
        context: Context,
        reminderType: String, // "workout" or "meal"
        hour: Int,
        minute: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, FitProReminderReceiver::class.java).apply {
            action = FitProReminderReceiver.ACTION_REMINDER
            putExtra(FitProReminderReceiver.EXTRA_REMINDER_TYPE, reminderType)
        }

        val requestCode = if (reminderType == "workout") 1001 else 1002

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            // Fallback for strict alarm permissions
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun cancelReminder(context: Context, reminderType: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, FitProReminderReceiver::class.java).apply {
            action = FitProReminderReceiver.ACTION_REMINDER
        }

        val requestCode = if (reminderType == "workout") 1001 else 1002

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        pendingIntent?.let {
            alarmManager.cancel(it)
            it.cancel()
        }
    }

    fun triggerInstantTestNotification(context: Context, reminderType: String) {
        val intent = Intent(context, FitProReminderReceiver::class.java).apply {
            action = FitProReminderReceiver.ACTION_REMINDER
            putExtra(FitProReminderReceiver.EXTRA_REMINDER_TYPE, reminderType)
            putExtra(
                FitProReminderReceiver.EXTRA_TITLE,
                if (reminderType == "workout") "🔔 Test: Daily Workout Reminder" else "🥗 Test: Daily Meal Logging Reminder"
            )
            putExtra(
                FitProReminderReceiver.EXTRA_MESSAGE,
                if (reminderType == "workout") "Your 08:00 AM workout notification is active! Time to crush today's set." else "Your meal logging notification is active! Keep track of your macros."
            )
        }
        context.sendBroadcast(intent)
    }
}
