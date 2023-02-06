package com.jiangnane.abus.server;

import static com.jiangnane.abus.core.Constant.BINDER_KEY;
import static com.jiangnane.abus.core.Constant.CALL_METHOD;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.jiangnane.abus.core.cache.BinderCacheManager;
import com.jiangnane.abus.utils.Logger;
import com.jiangnane.abus.utils.compat.BundleCompat;

/**
 * Provider承载类
 * <p>
 * Created by hanwei on 22/10/8.
 * <p>
 *
 * 主要通过Call方法建立binder通道，此处提供了client和server两个通道的建立机制
 */
public class BinderProvider extends ContentProvider {

    private static final String TAG = BinderProvider.class.getSimpleName();
    private final BinderBridge mBinderBridge = new BinderBridge();

    @Override
    public boolean onCreate() {
        Context context = getContext();
        DaemonService.startup(context);
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }


    /**
     * 在call中检测签名，只有通道建立时检测一次，不会影响通信性能
     * @param method
     * @param arg
     * @param extras
     * @return
     */
    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        if (method != null && method.startsWith(CALL_METHOD)) {
            Bundle bundle = new Bundle();
            BundleCompat.putBinder(bundle, BINDER_KEY, mBinderBridge);
            return bundle;
        }
        return null;
    }

    /**
     * Binder桥, 建立通道后，实现基本通信接口的定义
     */
    private class BinderBridge extends IBinderBridge.Stub {

        @Override
        public void addClientBinder(String name, IBinder binder) throws RemoteException {
            if (name != null && binder != null) {
                Logger.printStack();
                BinderCacheManager.INS.addClientBinder(name, binder);
                Logger.i(TAG, "addClientBinder");
            }
        }

        @Override
        public void removeClientBinder(String name) throws RemoteException {
            if (name != null) {
                BinderCacheManager.INS.removeClientBinder(name);
                Logger.i(TAG, "removeClientBinder");
            }
        }

        @Override
        public void post(String key, Bundle result) throws RemoteException {
            if (result != null) {
                Logger.printStack();
                Logger.i(TAG, "Post key=" + key + ", result=" + result);
                BinderCacheManager.INS.postEvent(key, result);
            }
        }

        @Override
        public void addServerBinder(String name, IBinder binder) throws RemoteException {
            Logger.printStack();
            BinderCacheManager.INS.addServerBinder(name, binder);
            Logger.i(TAG, "addServerBinder");
        }

        @Override
        public void removeServerBinder(String name) throws RemoteException {
            BinderCacheManager.INS.removeServerBinder(name);
            Logger.i(TAG, "removeServerBinder");
        }

        @Override
        public Bundle call(int method, String key, Bundle params) throws RemoteException {
            Logger.printStack();
            return BinderCacheManager.INS.call(method, key, params);
        }
    }
}
