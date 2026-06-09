package com.example.fitlife;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 封装提醒相关的 SharedPreferences 读写，避免 key 散落在多个类里。
 */
final class ReminderPreferences {
    private static final String NAME = "fitlife_reminders";
    private static final String EXERCISE_ITEMS = "exercise_items";
    private static final String DEFAULT_EXERCISE_ITEMS = "快走、拉伸";

    private ReminderPreferences() {
    }

    static SharedPreferences open(Context context) {
        return context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    /**
     * 首次启动时写入默认提醒时间和默认运动项目。
     */
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
        // 即使旧版本没有写入默认值，也能用兜底时间正常显示和调度。
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
        // 每种提醒使用自己的 key 前缀，避免运动和饮食配置互相覆盖。
        return type.key + "_hour";
    }

    private static String minuteKey(ReminderType type) {
        return type.key + "_minute";
    }

    private static String enabledKey(ReminderType type) {
        return type.key + "_enabled";
    }
}
