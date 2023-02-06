package com.jiangnane.abus.core.server;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.jiangnane.abus.core.cache.SubscriberCacheManager;
import com.jiangnane.abus.server.IClient;
import com.jiangnane.abus.utils.Logger;

/**
 * 用于实现客户端订阅机制的Binder通道客户端
 * <p>
 * Created by hanwei on 22/10/8.
 * <p>
 * 通过BinderClient可以将订阅的信号分发到SubscriberCacheManager
 */
public class BinderClient extends IClient.Stub {

    private static final BinderClient CLIENT = new BinderClient();
    private Handler mHandler;

    private BinderClient() {
        mHandler = new Handler(Looper.myLooper());
    }

    public static final BinderClient get() {
        return CLIENT;
    }

    @Override
    public void onEventReceive(String key, Bundle event) {
        Logger.printStack();
        mHandler.post(() -> {
            SubscriberCacheManager.INS.onEvent(key, event);
            Logger.printStack();
        });
    }
}
