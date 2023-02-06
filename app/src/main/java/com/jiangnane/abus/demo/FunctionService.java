package com.jiangnane.abus.demo;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.jiangnane.abus.ABus;
import com.jiangnane.abus.core.RequestListener;
import com.jiangnane.abus.utils.Logger;

public class FunctionService extends Service {

    private static final String NOTIFY_ID = "FunctionService";
    private static final String TAG = FunctionService.class.getSimpleName();

    private static final String HVAC_KEY = "hvac";

    public static void startup(Context context) {
        context.startService(new Intent(context, FunctionService.class));
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
        FunctionService.startForeground(this);

        ABus.INS.registerServer("hvac", new RequestListener() {

            boolean isHvacOn = false;

            @Override
            public Bundle onRequest(int method, String key, Bundle params) throws RemoteException {
                if(method == METHOD_GET) {
                    if(HVAC_KEY.equals(key)) {
                        Bundle b = new Bundle();
                        b.putBoolean("hvac_switch", isHvacOn);
                        return b;
                    }
                } else if (method == METHOD_SET) {
                    isHvacOn = params != null && params.getBoolean("hvac_switch");
                    return null;
                }

                return super.onRequest(method, key, params);
            }
        });

        ABus.INS.subscribe("hvac", (eventName, event) -> {
            switch (eventName) {
                case "hvac": {
                    boolean isHvacOn = event != null && event.getBoolean("hvac_switch");
                    Logger.i(TAG, "The hvac_switch value is " + isHvacOn + ". ");
                }
                break;
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
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
