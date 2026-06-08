package com.example.fitlife;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import java.util.Calendar;

final class ReminderScheduler {
    static final String EXTRA_TYPE = "reminder_type";

    private final Context context;
    private final AlarmManager alarmManager;

    ReminderScheduler(Context context) {
        this.context = context.getApplicationContext();
        alarmManager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
    }

    void scheduleDaily(ReminderType type) {
        SharedPreferences preferences = ReminderPreferences.open(context);
        Calendar nextTime = Calendar.getInstance();
        nextTime.set(Calendar.HOUR_OF_DAY, ReminderPreferences.getHour(preferences, type));
        nextTime.set(Calendar.MINUTE, ReminderPreferences.getMinute(preferences, type));
        nextTime.set(Calendar.SECOND, 0);
        nextTime.set(Calendar.MILLISECOND, 0);

        if (nextTime.getTimeInMillis() <= System.currentTimeMillis()) {
            nextTime.add(Calendar.DAY_OF_YEAR, 1);
        }

        PendingIntent intent = pendingIntent(type);
        if (canUseExactAlarms() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTime.getTimeInMillis(), intent);
        } else if (canUseExactAlarms()) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextTime.getTimeInMillis(), intent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextTime.getTimeInMillis(), intent);
        }
    }

    void cancel(ReminderType type) {
        alarmManager.cancel(pendingIntent(type));
    }

    private PendingIntent pendingIntent(ReminderType type) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra(EXTRA_TYPE, type.name());
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        return PendingIntent.getBroadcast(context, type.requestCode, intent, flags);
    }

    private boolean canUseExactAlarms() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms();
    }
}
