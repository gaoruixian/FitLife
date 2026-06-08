package com.example.fitlife;

import android.content.Context;
import android.content.SharedPreferences;

final class ReminderPreferences {
    private static final String NAME = "fitlife_reminders";
    private static final String EXERCISE_ITEMS = "exercise_items";
    private static final String DEFAULT_EXERCISE_ITEMS = "快走、拉伸";

    private ReminderPreferences() {
    }

    static SharedPreferences open(Context context) {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    static void ensureDefaults(SharedPreferences preferences) {
        if (!preferences.contains(hourKey(ReminderType.EXERCISE))) {
            preferences.edit()
                    .putInt(hourKey(ReminderType.EXERCISE), 19)
                    .putInt(minuteKey(ReminderType.EXERCISE), 30)
                    .putBoolean(enabledKey(ReminderType.EXERCISE), false)
                    .putString(EXERCISE_ITEMS, DEFAULT_EXERCISE_ITEMS)
                    .putInt(hourKey(ReminderType.DIET), 12)
                    .putInt(minuteKey(ReminderType.DIET), 0)
                    .putBoolean(enabledKey(ReminderType.DIET), false)
                    .apply();
        }
    }

    static int getHour(SharedPreferences preferences, ReminderType type) {
        return preferences.getInt(hourKey(type), type == ReminderType.EXERCISE ? 19 : 12);
    }

    static int getMinute(SharedPreferences preferences, ReminderType type) {
        return preferences.getInt(minuteKey(type), type == ReminderType.EXERCISE ? 30 : 0);
    }

    static boolean isEnabled(SharedPreferences preferences, ReminderType type) {
        return preferences.getBoolean(enabledKey(type), false);
    }

    static void setTime(SharedPreferences preferences, ReminderType type, int hour, int minute) {
        preferences.edit()
                .putInt(hourKey(type), hour)
                .putInt(minuteKey(type), minute)
                .apply();
    }

    static void setEnabled(SharedPreferences preferences, ReminderType type, boolean enabled) {
        preferences.edit()
                .putBoolean(enabledKey(type), enabled)
                .apply();
    }

    static String getExerciseItems(SharedPreferences preferences) {
        return preferences.getString(EXERCISE_ITEMS, DEFAULT_EXERCISE_ITEMS);
    }

    static void setExerciseItems(SharedPreferences preferences, String items) {
        preferences.edit()
                .putString(EXERCISE_ITEMS, items)
                .apply();
    }

    private static String hourKey(ReminderType type) {
        return type.key + "_hour";
    }

    private static String minuteKey(ReminderType type) {
        return type.key + "_minute";
    }

    private static String enabledKey(ReminderType type) {
        return type.key + "_enabled";
    }
}
