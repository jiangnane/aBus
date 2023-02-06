package com.jiangnane.abus.core.cache;

import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.IBinder;
import android.os.RemoteException;

import com.jiangnane.abus.server.IClient;
import com.jiangnane.abus.server.IServer;
import com.jiangnane.abus.utils.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BinderCacheManager
 * <p>
 * Created by hanwei on 22/10/8.
 * <p>
 * 实现BinderCache，分别缓存Client和Server的连接
 */
public enum BinderCacheManager {
    INS;
    private static final String TAG = BinderCacheManager.class.getSimpleName();

    /**
     * 客户端binder缓存
     */
    private final Map<String, IBinder> mClientCache = new ConcurrentHashMap<>(5);

    public synchronized void addClientBinder(String name, IBinder clientBinder) {
        mClientCache.put(name, clientBinder);
    }

    public synchronized IBinder removeClientBinder(String name) {
        return mClientCache.remove(name);
    }

    public synchronized IBinder getClientBinder(String name) {
        return mClientCache.get(name);
    }


    /**
     * 对Client订阅的事件进行分发(广播性质)<p>
     * 所有建立的Client连接的客户端都会收到消息，但是会在Client侧进行筛选
     * @param key 事件名称
     * @param event 事件参数
     */
    public synchronized void postEvent(String key, Bundle event) {
        if (mClientCache.isEmpty()) {
            Logger.i(TAG, "mClientCache is empty.");
            return;
        }
        for (Map.Entry<String, IBinder> entry : mClientCache.entrySet()) {
            IBinder clientBinder = entry.getValue();
            if (clientBinder.isBinderAlive()) {
                IClient client = IClient.Stub.asInterface(clientBinder);
                try {
                    client.onEventReceive(key, event);
                } catch (RemoteException e) {
                    Logger.w(TAG, "onEventReceive error:" + e);
//                    removeClientBinder(entry.getKey());
                }
            } else {
                Logger.i(TAG, "binder error: " + clientBinder);
                removeClientBinder(entry.getKey()); // 如果client已经不在活跃态则直接删除缓存
            }
        }
    }

    /*==================以下是服务端接口实现=====================*/

    private final Map<String, IBinder> mServerCache = new ConcurrentHashMap<>(5);

    public synchronized void addServerBinder(String name, IBinder serverBinder) {
        mServerCache.put(name, serverBinder);
    }

    public synchronized IBinder removeServerBinder(String name) {
        return mServerCache.remove(name);
    }

    public synchronized IBinder getServerBinder(String name) {
        return mServerCache.get(name);
    }

    /**
     * 将get/set请求传递给服务端
     * 因为信号的服务端实现是唯一的，所以只获取cache中的key相同的binder并通信
     * @param method
     * @param key
     * @param params
     * @return
     */
    public synchronized Bundle call(int method, String key, Bundle params) {
        if (mServerCache.isEmpty()) {
            Logger.i(TAG, "No servers ready for processing this request.");
            return null;
        }

        IBinder serverBinder = getServerBinder(key);
        if (serverBinder != null && serverBinder.isBinderAlive()) {
            IServer server = IServer.Stub.asInterface(serverBinder);
            try {
                Logger.printStack();
                return server.onRequest(method, key, params);
            } catch (RemoteException e) {
                Logger.w(TAG, "onGetRequest error:" + e);
                if(e instanceof DeadObjectException) {
                    removeServerBinder(key);
                } else {
                    throw new RuntimeException(e); // 其它错误直接扔出RuntimeException
                }
            }
            Logger.printStack();
        } else {
            Logger.i(TAG, "binder error: " + serverBinder);
            removeServerBinder(key);
        }

        return null;
    }
}
