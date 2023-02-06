package com.jiangnane.abus.core;


import static com.jiangnane.abus.core.Constant.BINDER_CLIENT_PREFIX;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.Process;

import com.jiangnane.abus.BuildConfig;
import com.jiangnane.abus.core.impl.DefaultGetter;
import com.jiangnane.abus.core.impl.DefaultPoster;
import com.jiangnane.abus.core.impl.DefaultSetter;
import com.jiangnane.abus.core.server.BinderClient;
import com.jiangnane.abus.core.server.BinderManager;
import com.jiangnane.abus.utils.AppUtil;
import com.jiangnane.abus.utils.Logger;

public enum BusInternal {
    INS;

    private static final String TAG = BusInternal.class.getSimpleName();

    private Context mContext;
    private IPoster mPoster;
    private IGetter mGetter;
    private ISetter mSetter;

    private boolean isClientAdded = false;

    BusInternal() {
    }

    public Context getContext() {
        return mContext;
    }

    public void init(Context context) {
        synchronized (BusInternal.class) {
            if (mContext == null) {
                if (Looper.myLooper() != Looper.getMainLooper()) {
                    throw new IllegalStateException("BusInternal.startup() must called in main thread.");
                }
                if(context instanceof Activity || context instanceof Service) {
                    mContext = context.getApplicationContext();
                } else {
                    mContext = context;
                }

                Logger.i(TAG, "---abus initiated: ver=" + BuildConfig.versionName + "---");
            }
        }
    }

    public void destroy() {
        if(mContext == null) {
            return;
        }

        unRegisterClient();

        unRegisterServerAll();

        Logger.i(TAG, "---destroyed---");
    }

    public void registerClient() {
        if(mContext == null) {
            return;
        }

        if(isClientAdded) {
            return;
        }

        BinderManager.INS.addClientBinder(getClientName(mContext), BinderClient.get());
        isClientAdded = true;

        Logger.i(TAG, "---BinderClient Added---");
    }

    private void unRegisterClient() {
        if(mContext == null) {
            return;
        }

        if(!isClientAdded) {
            return;
        }

        BinderManager.INS.removeClientBinder(getClientName(mContext));
        isClientAdded = false;

        Logger.i(TAG, "---BinderClient Removed---");
    }

    public void registerServer(String moduleName, RequestListener binderServer) {
        if(mContext == null) {
            return;
        }

        BinderManager.INS.addServerBinder(moduleName, binderServer);

        Logger.i(TAG, "---BinderServer Added---");
    }

    public void unRegisterServer(String moduleName) {
        if(mContext == null) {
            return;
        }

        BinderManager.INS.removeServerBinder(moduleName);

        Logger.i(TAG, "---BinderServer Removed---");
    }

    public void unRegisterServerAll() {
        if(mContext == null) {
            return;
        }

        BinderManager.INS.removeAllServerBinders();

        Logger.i(TAG, "---BinderServer All Removed---");
    }

    public void post(String key, Bundle event) {
        if(mPoster == null) {
            mPoster = new DefaultPoster();
        }

        mPoster.post(key, event);
    }

    public Bundle get(String key, Bundle params) {
        if(mGetter == null) {
            mGetter = new DefaultGetter();
        }

        return mGetter.get(key, params);
    }

    public void set(String key, Bundle params) {
        if(mSetter == null) {
            mSetter = new DefaultSetter();
        }

        mSetter.set(key, params);
    }

    public static String getClientName(Context context) {
        return BINDER_CLIENT_PREFIX + AppUtil.getProcessName(context, Process.myPid());
    }
}
