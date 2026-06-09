package com.example.fitlife;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * 设备重启后恢复已经开启的提醒。
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            return;
        }

        SharedPreferences preferences = ReminderPreferences.open(context);
        ReminderScheduler scheduler = new ReminderScheduler(context);
        // 系统重启会清空 AlarmManager 中的闹钟，因此需要按用户配置重新注册。
        for (ReminderType type : ReminderType.values()) {
            if (ReminderPreferences.isEnabled(preferences, type)) {
                scheduler.scheduleDaily(type);
            }
        }
    }
}
