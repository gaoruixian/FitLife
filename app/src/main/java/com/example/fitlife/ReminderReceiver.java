package com.example.fitlife;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

/**
 * 接收系统闹钟广播，展示通知，并安排下一次每日提醒。
 */
public class ReminderReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "daily_reminders";

    @Override
    public void onReceive(Context context, Intent intent) {
        ReminderType type = readType(intent);
        if (type == null) {
            return;
        }

        showNotification(context, type);
        // AlarmManager 的一次性闹钟触发后不会自动重复，这里重新安排下一天。
        new ReminderScheduler(context).scheduleDaily(type);
    }

    private ReminderType readType(Intent intent) {
        // 广播中带有提醒类型；解析失败时直接忽略，避免错误通知。
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

        // 点击通知回到首页，方便用户继续调整提醒。
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
        // 运动提醒使用用户在项目页勾选的内容，让提醒更个性化。
        SharedPreferences preferences = ReminderPreferences.open(context);
        return "今天可以做：" + ReminderPreferences.getExerciseItems(preferences);
    }

    private void createChannel(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Android 8+ 必须先创建通知渠道，否则通知不会显示。
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
