package com.jiangnane.abus.core;

import android.os.Bundle;
import android.os.RemoteException;

import com.jiangnane.abus.server.IServer;

/**
 * binder服务端通道建立后的通信接口类
 * <p>
 * Created by hanwei on 22/10/8.
 * <p>
 */
public abstract class RequestListener extends IServer.Stub {
    /**
     * 通信方法：获取数据
     */
    public static final int METHOD_GET = 0;

    /**
     * 通信方法：写入数据
     */
    public static final int METHOD_SET = 1;

    /**
     * 通信回调接口
     * @param method 通信方法
     * @param key 事件关键字
     * @param params 事件参数
     * @return 返回结果
     * @throws RemoteException
     */
    @Override
    public Bundle onRequest(int method, String key, Bundle params) throws RemoteException {
        return null;
    }
}
