package com.example.fitlife;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 运动项目选择页，支持勾选适合自己的运动，并查看每项运动的运动量建议。
 */
public class ExerciseOptionsActivity extends Activity {
    private SharedPreferences preferences;
    private CheckBox walkCheckBox;
    private CheckBox runningCheckBox;
    private CheckBox stretchingCheckBox;
    private CheckBox strengthCheckBox;
    private CheckBox yogaCheckBox;
    private CheckBox cyclingCheckBox;
    private CheckBox ropeCheckBox;
    private CheckBox coreCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_options);

        preferences = ReminderPreferences.open(this);
        ReminderPreferences.ensureDefaults(preferences);

        // 页面使用一组 CheckBox 保存用户选择；点击整行则只展示建议，不直接切换选中状态。
        TextView backButton = findViewById(R.id.back_button);
        walkCheckBox = findViewById(R.id.walk_check_box);
        runningCheckBox = findViewById(R.id.running_check_box);
        stretchingCheckBox = findViewById(R.id.stretching_check_box);
        strengthCheckBox = findViewById(R.id.strength_check_box);
        yogaCheckBox = findViewById(R.id.yoga_check_box);
        cyclingCheckBox = findViewById(R.id.cycling_check_box);
        ropeCheckBox = findViewById(R.id.rope_check_box);
        coreCheckBox = findViewById(R.id.core_check_box);
        Button saveButton = findViewById(R.id.save_exercise_options_button);

        renderSelections();
        bindSuggestion(R.id.walk_row, R.string.exercise_walk, R.string.exercise_walk_suggestion);
        bindSuggestion(R.id.running_row, R.string.exercise_running, R.string.exercise_running_suggestion);
        bindSuggestion(R.id.stretching_row, R.string.exercise_stretching, R.string.exercise_stretching_suggestion);
        bindSuggestion(R.id.strength_row, R.string.exercise_strength, R.string.exercise_strength_suggestion);
        bindSuggestion(R.id.yoga_row, R.string.exercise_yoga, R.string.exercise_yoga_suggestion);
        bindSuggestion(R.id.cycling_row, R.string.exercise_cycling, R.string.exercise_cycling_suggestion);
        bindSuggestion(R.id.rope_row, R.string.exercise_rope, R.string.exercise_rope_suggestion);
        bindSuggestion(R.id.core_row, R.string.exercise_core, R.string.exercise_core_suggestion);
        backButton.setOnClickListener(v -> finish());
        saveButton.setOnClickListener(v -> saveSelections());
    }

    /**
     * 绑定运动项目行点击事件，展示对应运动量建议。
     */
    private void bindSuggestion(int rowId, int titleId, int suggestionId) {
        View row = findViewById(rowId);
        row.setOnClickListener(v -> new AlertDialog.Builder(this)
                .setTitle(titleId)
                .setMessage(suggestionId)
                .setPositiveButton(R.string.got_it, null)
                .show());
    }

    private void renderSelections() {
        // 当前项目以中文顿号拼接保存，渲染时按项目名称判断是否已选。
        String selected = ReminderPreferences.getExerciseItems(preferences);
        walkCheckBox.setChecked(selected.contains("快走"));
        runningCheckBox.setChecked(selected.contains("慢跑"));
        stretchingCheckBox.setChecked(selected.contains("拉伸"));
        strengthCheckBox.setChecked(selected.contains("力量训练"));
        yogaCheckBox.setChecked(selected.contains("瑜伽"));
        cyclingCheckBox.setChecked(selected.contains("骑行"));
        ropeCheckBox.setChecked(selected.contains("跳绳"));
        coreCheckBox.setChecked(selected.contains("核心训练"));
    }

    private void saveSelections() {
        StringBuilder builder = new StringBuilder();
        appendIfChecked(builder, walkCheckBox, "快走");
        appendIfChecked(builder, runningCheckBox, "慢跑");
        appendIfChecked(builder, stretchingCheckBox, "拉伸");
        appendIfChecked(builder, strengthCheckBox, "力量训练");
        appendIfChecked(builder, yogaCheckBox, "瑜伽");
        appendIfChecked(builder, cyclingCheckBox, "骑行");
        appendIfChecked(builder, ropeCheckBox, "跳绳");
        appendIfChecked(builder, coreCheckBox, "核心训练");

        if (builder.length() == 0) {
            Toast.makeText(this, "至少选择一个运动项目", Toast.LENGTH_SHORT).show();
            return;
        }

        ReminderPreferences.setExerciseItems(preferences, builder.toString());
        // 保存运动项目后默认开启运动提醒，让用户设置完即可收到提醒。
        ReminderPreferences.setEnabled(preferences, ReminderType.EXERCISE, true);
        new ReminderScheduler(this).scheduleDaily(ReminderType.EXERCISE);
        Toast.makeText(this, "运动项目已保存", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void appendIfChecked(StringBuilder builder, CheckBox checkBox, String value) {
        if (!checkBox.isChecked()) {
            return;
        }
        // 通知和首页摘要使用“快走、拉伸”这种可读性更好的展示格式。
        if (builder.length() > 0) {
            builder.append("、");
        }
        builder.append(value);
    }
}
