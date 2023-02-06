package com.jiangnane.abus.core.server;

import static com.jiangnane.abus.core.Constant.BINDER_KEY;
import static com.jiangnane.abus.core.Constant.CALL_METHOD;
import static com.jiangnane.abus.core.Constant.SERVICE_CP_AUTH;
import static com.jiangnane.abus.core.RequestListener.METHOD_GET;
import static com.jiangnane.abus.core.RequestListener.METHOD_SET;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;

import com.jiangnane.abus.core.BusInternal;
import com.jiangnane.abus.server.IBinderBridge;
import com.jiangnane.abus.utils.Logger;
import com.jiangnane.abus.utils.compat.BundleCompat;
import com.jiangnane.abus.utils.compat.ContentProviderCompat;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Binder管理器
 * <p>
 * Created by hanwei on 22/10/8.
 * <p>
 * 此处缓存服务端连接通道的引用，用以提供连接保持服务
 */
public enum BinderManager {
    INS;

    private static final String TAG = BinderManager.class.getSimpleName();

    private final Map<String, IBinder> mServerCache = new ConcurrentHashMap<>(5);
    private final Map<String, IBinder> mClientCache = new ConcurrentHashMap<>(5);

    private IBinderBridge mBinderBridge;

    private Handler mInitHandler;

    private IBinderBridge getBinderBridge() {
        if (mBinderBridge == null || !mBinderBridge.asBinder().isBinderAlive()) {
            synchronized (BinderManager.class) {
                mInitHandler = new Handler(Looper.getMainLooper());
                Context context = BusInternal.INS.getContext();

                Logger.printStack();
                Bundle response = ContentProviderCompat.call(context, SERVICE_CP_AUTH, CALL_METHOD);

                if (response != null) {
                    IBinder binder = BundleCompat.getBinder(response, BINDER_KEY);
                    linkBinderDied(binder);
                    mBinderBridge = IBinderBridge.Stub.asInterface(binder);
                }
            }
        }
        return mBinderBridge;
    }

    public void addClientBinder(String name, IBinder binder) {
        IBinderBridge bridge = getBinderBridge();
        if (bridge != null) {
            try {
                Logger.printStack();
                bridge.addClientBinder(name, binder);
                mClientCache.put(name, binder);
            } catch (RemoteException e) {
                Logger.w(TAG, "addBinder: " + e.getLocalizedMessage());
            }
        }
    }

    public void removeClientBinder(String name) {
        IBinderBridge bridge = getBinderBridge();
        if (bridge != null) {
            try {
                bridge.removeClientBinder(name);
                mClientCache.remove(name);
            } catch (RemoteException e) {
                Logger.w(TAG, "removeBinder: " + e.getLocalizedMessage());
            }
        }
    }

    /**
     * post分发接口
     * @param key
     * @param extra
     */
    public void post(String key, Bundle extra) {
        IBinderBridge bridge = getBinderBridge();
        if (bridge != null) {
            try {
//                if (extra != null) {
//                    extra.putLong(POST_DUR_MEASURE, System.currentTimeMillis());
//                }
                Logger.printStack();
                bridge.post(key, extra);
            } catch (RemoteException e) {
                Logger.w(TAG, "post: " + e.getLocalizedMessage());
            }
        }
    }

    public void addServerBinder(String name, IBinder binder) {
        IBinderBridge bridge = getBinderBridge();
        if (bridge != null) {
            try {
                Logger.printStack();
                bridge.addServerBinder(name, binder);
                mServerCache.put(name, binder);
            } catch (RemoteException e) {
                Logger.w(TAG, "addBinder: " + e.getLocalizedMessage());
            }
        }
    }

    public void removeServerBinder(String name) {
        IBinderBridge bridge = getBinderBridge();
        if (bridge != null) {
            try {
                bridge.removeServerBinder(name);
                mServerCache.remove(name);
            } catch (RemoteException e) {
                Logger.w(TAG, "removeBinder: " + e.getLocalizedMessage());
            }
        }
    }

    public void removeAllServerBinders() {
        IBinderBridge bridge = getBinderBridge();
        if (bridge != null && !mServerCache.isEmpty()) {
            Set<String> keys = mServerCache.keySet();
            for(String key : keys) {
                try {
                    bridge.removeServerBinder(key);
                } catch (RemoteException e) {
                    Logger.w(TAG, "removeBinder: " + e.getLocalizedMessage());
                }
            }
            mServerCache.clear();
        }
    }

    /**
     * get获取接口
     * @param key
     * @param params
     * @return
     */
    public Bundle get(String key, Bundle params) {
        IBinderBridge bridge = getBinderBridge();
        if (bridge != null) {
            try {
                Logger.printStack();
                return bridge.call(METHOD_GET, key, params);
            } catch (RemoteException e) {
                Logger.w(TAG, "post: " + e.getLocalizedMessage());
            }
        }

        return null;
    }

    /**
     * set设置接口
     * @param key
     * @param params
     */
    public void set(String key, Bundle params) {
        IBinderBridge bridge = getBinderBridge();
        if (bridge != null) {
            try {
                Logger.printStack();
                bridge.call(METHOD_SET, key, params);
            } catch (RemoteException e) {
                Logger.w(TAG, "post: " + e.getLocalizedMessage());
            }
        }
    }

    /**
     * 监听Binder的DeathRecipient，当检测到服务断开后自动重连<p>
     * (目标进程为调用registerServer的进程，也就是提供服务的模块进程)
     * @param binder
     */
    private void linkBinderDied(final IBinder binder) {
        IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
            @Override
            public void binderDied() {
                reconnectServers();
                reconnectClients();
                binder.unlinkToDeath(this, 0);
                Logger.i(TAG, "binderDied: " + binder);
            }
        };

        try {
            binder.linkToDeath(deathRecipient, 0);
        } catch (RemoteException e) {
            Logger.w(TAG, "linkBinderDied: " + e.getLocalizedMessage());
        }
    }

    /**
     * 自动重连服务端连接，用以提供稳定的get/set方法
     */
    private void reconnectServers() {
        if(mInitHandler != null) {
            mInitHandler.postDelayed(() -> {
                if(mServerCache.isEmpty()) {
                    return;
                }

                Set<Map.Entry<String, IBinder>> entries = mServerCache.entrySet();
                for(Map.Entry<String, IBinder> entry : entries) {
                    addServerBinder(entry.getKey(), entry.getValue());
                    Logger.i(TAG, "reconnectServers: add " + entry);
                }
            }, 100);
        }
    }

    /**
     * 自动重连客户端连接，用以提供稳定的监听listen方法
     */
    private void reconnectClients() {
        if(mInitHandler != null) {
            mInitHandler.postDelayed(() -> {
                if(mClientCache.isEmpty()) {
                    return;
                }

                Set<Map.Entry<String, IBinder>> entries = mClientCache.entrySet();
                for(Map.Entry<String, IBinder> entry : entries) {
                    addClientBinder(entry.getKey(), entry.getValue());
                    Logger.i(TAG, "reconnectClients: add " + entry);
                }
            }, 100);
        }
    }
}
