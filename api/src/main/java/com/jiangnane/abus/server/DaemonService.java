package com.jiangnane.abus.server;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

/**
 * Binder进程的保活服务，和Provider同一个进程
 * <p>
 * Created by hanwei on 22/10/8.
 */
public class DaemonService extends Service {

    private static final String NOTIFY_ID = "1001";

    public static void startup(Context context) {
        context.startService(new Intent(context, DaemonService.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        startup(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startService(new Intent(this, InnerService.class));
        DaemonService.startForeground(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    public static final class InnerService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            DaemonService.startForeground(this);
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }


    public static void startForeground(Service context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(NOTIFY_ID, "DaemonService", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(context.getApplicationContext(), NOTIFY_ID).build();
            context.startForeground(1, notification);
        }
    }
}
