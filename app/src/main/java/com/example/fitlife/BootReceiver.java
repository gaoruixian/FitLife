package com.example.fitlife;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            return;
        }

        SharedPreferences preferences = ReminderPreferences.open(context);
        ReminderScheduler scheduler = new ReminderScheduler(context);
        for (ReminderType type : ReminderType.values()) {
            if (ReminderPreferences.isEnabled(preferences, type)) {
                scheduler.scheduleDaily(type);
            }
        }
    }
}
