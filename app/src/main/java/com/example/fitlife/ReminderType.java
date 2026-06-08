package com.example.fitlife;

enum ReminderType {
    EXERCISE(2001, "exercise", "运动", "该活动一下啦", "站起来伸展或完成今天的训练计划。"),
    DIET(2002, "diet", "饮食", "饮食时间到", "记得按时吃饭、喝水，并选择更健康的食物。");

    final int requestCode;
    final String key;
    final String displayName;
    final String notificationTitle;
    final String notificationText;

    ReminderType(int requestCode, String key, String displayName, String notificationTitle, String notificationText) {
        this.requestCode = requestCode;
        this.key = key;
        this.displayName = displayName;
        this.notificationTitle = notificationTitle;
        this.notificationText = notificationText;
    }
}
