package com.example.fitlife;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

public class ReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "daily_reminders";

    @Override
    public void onReceive(Context context, Intent intent) {
        ReminderType type = readType(intent);
        if (type == null) {
            return;
        }

        showNotification(context, type);
        new ReminderScheduler(context).scheduleDaily(type);
    }

    private ReminderType readType(Intent intent) {
        if (intent == null) {
            return null;
        }
        String value = intent.getStringExtra(ReminderScheduler.EXTRA_TYPE);
        if (value == null) {
            return null;
        }
        try {
            return ReminderType.valueOf(value);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private void showNotification(Context context, ReminderType type) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager == null) {
            return;
        }

        createChannel(manager);

        Intent openAppIntent = new Intent(context, MainActivity.class);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent openAppPendingIntent = PendingIntent.getActivity(context, type.requestCode, openAppIntent, flags);

        android.app.Notification.Builder builder = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                ? new android.app.Notification.Builder(context, CHANNEL_ID)
                : new android.app.Notification.Builder(context);

        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(type.notificationTitle)
                .setContentText(notificationText(context, type))
                .setContentIntent(openAppPendingIntent)
                .setAutoCancel(true);

        manager.notify(type.requestCode, builder.build());
    }

    private String notificationText(Context context, ReminderType type) {
        if (type != ReminderType.EXERCISE) {
            return type.notificationText;
        }
        SharedPreferences preferences = ReminderPreferences.open(context);
        return "今天可以做：" + ReminderPreferences.getExerciseItems(preferences);
    }

    private void createChannel(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "每日提醒",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("运动和饮食提醒通知");
            manager.createNotificationChannel(channel);
        }
    }
}
