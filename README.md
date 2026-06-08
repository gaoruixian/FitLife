# FitLife

FitLife 是一个原生 Android 示例 App，主要提供两个功能：

- 每日运动提醒：设置每天固定时间通知运动、伸展或训练。
- 饮食提醒：设置每天固定时间通知吃饭、喝水或健康饮食。

## 运行方式

1. 用 Android Studio 打开当前目录。
2. 等待 Gradle Sync 完成。
3. 连接安卓设备或启动模拟器。
4. 点击 Run 安装运行。

首次运行时，Android 13 及以上系统会请求通知权限；请允许，否则系统不会展示提醒通知。

## 项目结构

- `app/src/main/java/com/example/fitlife/MainActivity.java`：主界面和用户操作。
- `app/src/main/java/com/example/fitlife/ReminderScheduler.java`：每日提醒调度。
- `app/src/main/java/com/example/fitlife/ReminderReceiver.java`：接收闹钟并显示通知。
- `app/src/main/java/com/example/fitlife/BootReceiver.java`：手机重启后恢复已开启提醒。
