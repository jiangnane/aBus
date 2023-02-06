package com.jiangnane.abus.utils.compat;

import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemClock;

import com.jiangnane.abus.utils.Logger;

/**
 * Provider适配
 * <p>
 * Created by hanwei on 22/10/8.
 * <p>
 * 包括不同版本provider建立通道的机制适配、签名验证、权限验证等工作
 */
public class ContentProviderCompat {

    private static final String TAG = ContentProviderCompat.class.getSimpleName();

    /**
     * 调用provider的call方法建立连接
     * @param context
     * @param authority
     * @param method
     * @return
     */
    public static Bundle call(Context context, String authority, String method) {
        return call(context, Uri.parse("content://" + authority), method, null, null);
    }

    /**
     * 调用provider的call方法建立连接
     * @param context
     * @param authority
     * @param method
     * @param arg
     * @param extras
     * @return
     */
    public static Bundle call(Context context, String authority, String method, String arg, Bundle extras) {
        return call(context, Uri.parse("content://" + authority), method, arg, extras);
    }

    /**
     * 调用provider的call方法建立连接
     * @param context
     * @param uri
     * @param method
     * @param arg
     * @param extras
     * @return
     */
    public static Bundle call(Context context, Uri uri, String method, String arg, Bundle extras) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return context.getContentResolver().call(uri, method, arg, extras);
        }

        Bundle res = null;

        ContentProviderClient client = crazyAcquireContentProvider(context, uri);
        if (client == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                context.getContentResolver().takePersistableUriPermission(uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                client = crazyAcquireContentProvider(context, uri);
            }
        }

        if (client != null) {
            try {
                res = client.call(method, arg, extras);
            } catch (RemoteException e) {
                Logger.w(TAG, e.getMessage());
            } finally {
                releaseQuietly(client);
            }
        } else {
            res = context.getContentResolver().call(uri, method, arg, extras);
        }

        return res;
    }

    /**
     * 适配不同版本Android的ProviderClient建立方法
     * @param context
     * @param uri
     * @return
     */
    private static ContentProviderClient acquireContentProviderClient(Context context, Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            return context.getContentResolver().acquireUnstableContentProviderClient(uri);
        }
        return context.getContentResolver().acquireContentProviderClient(uri);
    }

    /**
     * 建立通道的连接重试机制
     * @param context
     * @param uri
     * @return
     */
    public static ContentProviderClient crazyAcquireContentProvider(Context context, Uri uri) {
        ContentProviderClient client = acquireContentProviderClient(context, uri);
        if (client == null) {
            int retry = 0;
            while (retry < 5 && client == null) {
                SystemClock.sleep(100);
                retry++;
                client = acquireContentProviderClient(context, uri);
            }
        }
        return client;
    }

    /**
     * 连接释放
     * @param client
     */
    public static void releaseQuietly(ContentProviderClient client) {
        if (client != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    client.close();
                } else {
                    client.release();
                }
            } catch (Exception ignored) {
            }
        }
    }
}
