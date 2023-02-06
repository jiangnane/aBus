package com.jiangnane.abus.demo;

import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.jiangnane.abus.ABus;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ABus.init(this);
        Log.i("wei.han", "Current pid: " + Process.myPid());
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FunctionService.startup(this);
    }
}
