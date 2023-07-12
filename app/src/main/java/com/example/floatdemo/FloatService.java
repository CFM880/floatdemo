package com.example.floatdemo;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;


import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class FloatService extends Service{


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 开启定时器，每隔0.5秒刷新一次
        Log.d("RemoteViewService", this.hashCode()+"");
        if (!MyWindowManager.isWindowShowing()) {
            MyWindowManager.createSmallWindow(getApplicationContext());
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Uri mUri = Settings.System.DEFAULT_NOTIFICATION_URI;
            NotificationChannel mChannel = new NotificationChannel("CHANNEL_ID", "TREADMILL_NAME", NotificationManager.IMPORTANCE_LOW);
            mChannel.setSound(mUri, Notification.AUDIO_ATTRIBUTES_DEFAULT);
            mNotiManager.createNotificationChannel(mChannel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "CHANNEL_ID");
            builder.setContentTitle(getString(R.string.app_name));
            builder.setContentText("");
            builder.setSmallIcon(R.drawable.ic_launcher_background);
            builder.setDefaults(Notification.DEFAULT_ALL);
            builder.setAutoCancel(true);
            builder.setShowWhen(true);
            startForeground(1, builder.build());
        } else {
            startForeground(1, buildForegroundNotification());
        }
        return super.onStartCommand(intent, flags, startId);
    }
    private Notification buildForegroundNotification() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setOngoing(false);
        builder.setContentTitle("")
                .setContentText("")
                .setTicker("");
        builder.setPriority(Notification.PRIORITY_MAX);
        return builder.build();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("RemoteViewService", this.hashCode()+"onDestroy");
        if (MyWindowManager.isWindowShowing()){
            MyWindowManager.removeSmallWindow(getApplicationContext());
        }
    }

    /**
     * 判断当前界面是否是桌面
     */
    private boolean isHome() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        return getHomes().contains(rti.get(0).topActivity.getPackageName());
    }

    /**
     * 获得属于桌面的应用的应用包名称
     *
     * @return 返回包含所有包名的字符串列表
     */
    private List<String> getHomes() {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }
}
