package com.example.fitlife;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * App 首页，负责展示运动/饮食提醒状态，并承接用户的主要操作。
 */
public class MainActivity extends Activity {
    private static final int REQUEST_NOTIFICATIONS = 1001;

    private ReminderScheduler reminderScheduler;
    private SharedPreferences preferences;
    private TextView exerciseTimeText;
    private TextView dietTimeText;
    private TextView exercisePlanSummaryText;
    private Switch exerciseSwitch;
    private Switch dietSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 首页和提醒接收器共用同一份本地配置，保证开关、时间和运动项目保持同步。
        reminderScheduler = new ReminderScheduler(this);
        preferences = ReminderPreferences.open(this);

        exerciseTimeText = findViewById(R.id.exercise_time_text);
        dietTimeText = findViewById(R.id.diet_time_text);
        exercisePlanSummaryText = findViewById(R.id.exercise_plan_summary_text);
        exerciseSwitch = findViewById(R.id.exercise_switch);
        dietSwitch = findViewById(R.id.diet_switch);
        View exerciseCard = findViewById(R.id.exercise_card);
        View exerciseMetricCard = findViewById(R.id.exercise_metric_card);
        Button exerciseTimeButton = findViewById(R.id.exercise_time_button);
        Button dietTimeButton = findViewById(R.id.diet_time_button);

        // 开关只负责启停提醒；具体提醒时间由时间选择器设置。
        exerciseSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ReminderPreferences.setEnabled(preferences, ReminderType.EXERCISE, isChecked);
            updateReminder(ReminderType.EXERCISE);
        });

        dietSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ReminderPreferences.setEnabled(preferences, ReminderType.DIET, isChecked);
            updateReminder(ReminderType.DIET);
        });

        exerciseTimeButton.setOnClickListener(v -> showTimePicker(ReminderType.EXERCISE));
        dietTimeButton.setOnClickListener(v -> showTimePicker(ReminderType.DIET));
        // 运动卡片点击进入项目选择页，时间按钮仍单独负责设置提醒时间。
        exerciseCard.setOnClickListener(v -> openExerciseOptions());
        exerciseMetricCard.setOnClickListener(v -> openExerciseOptions());

        ReminderPreferences.ensureDefaults(preferences);
        requestNotificationPermissionIfNeeded();
        renderState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (preferences != null) {
            // 从运动项目页返回时刷新“已选项目”和开关状态。
            renderState();
        }
    }

    /**
     * 弹出 24 小时时间选择器；选定时间后自动开启对应提醒。
     */
    private void showTimePicker(ReminderType type) {
        int hour = ReminderPreferences.getHour(preferences, type);
        int minute = ReminderPreferences.getMinute(preferences, type);

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    ReminderPreferences.setTime(preferences, type, selectedHour, selectedMinute);
                    ReminderPreferences.setEnabled(preferences, type, true);
                    renderState();
                    updateReminder(type);
                },
                hour,
                minute,
                true
        );
        dialog.show();
    }

    private void updateReminder(ReminderType type) {
        if (ReminderPreferences.isEnabled(preferences, type)) {
            // AlarmManager 的闹钟在触发后会由 Receiver 再次安排下一天。
            reminderScheduler.scheduleDaily(type);
            Toast.makeText(this, type.displayName + "提醒已开启", Toast.LENGTH_SHORT).show();
        } else {
            reminderScheduler.cancel(type);
            Toast.makeText(this, type.displayName + "提醒已关闭", Toast.LENGTH_SHORT).show();
        }
        renderState();
    }

    private void renderState() {
        boolean exerciseEnabled = ReminderPreferences.isEnabled(preferences, ReminderType.EXERCISE);
        boolean dietEnabled = ReminderPreferences.isEnabled(preferences, ReminderType.DIET);
        // 仅在状态不一致时更新 Switch，避免 renderState 反复触发监听器和 Toast。
        if (exerciseSwitch.isChecked() != exerciseEnabled) {
            exerciseSwitch.setChecked(exerciseEnabled);
        }
        if (dietSwitch.isChecked() != dietEnabled) {
            dietSwitch.setChecked(dietEnabled);
        }
        exerciseTimeText.setText(formatTime(ReminderType.EXERCISE));
        dietTimeText.setText(formatTime(ReminderType.DIET));
        exercisePlanSummaryText.setText("已选：" + ReminderPreferences.getExerciseItems(preferences));
    }

    private void openExerciseOptions() {
        startActivity(new Intent(this, ExerciseOptionsActivity.class));
    }

    private String formatTime(ReminderType type) {
        return String.format(
                Locale.getDefault(),
                "%02d:%02d",
                ReminderPreferences.getHour(preferences, type),
                ReminderPreferences.getMinute(preferences, type)
        );
    }

    private void requestNotificationPermissionIfNeeded() {
        // Android 13+ 需要运行时申请通知权限，否则提醒能触发但通知不会展示。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATIONS);
        }
    }
}
