package com.jiangnane.abus;

import android.content.Context;
import android.os.Bundle;

import com.jiangnane.abus.core.BusInternal;
import com.jiangnane.abus.core.EventListener;
import com.jiangnane.abus.core.RequestListener;
import com.jiangnane.abus.core.cache.SubscriberCacheManager;

/**
 * ABus(跨进程总线) API
 * <p>
 * Created by hanwei on 22/10/8.
 * <p>
 *
 * 此总线主要包含两种通信方式——同步通信和异步通信<p>
 * 异步通信的主要过程为：<p>
 * 调用subscribe方法订阅事件，调用post方法发送事件<p>
 * 同步通信的主要过程为：<p>
 * 调用get/set方法获取/设置数据，调用registerServer方法实现获取/设置数据的方法<p>
 */
public enum ABus {
    INS;

    private boolean isClientRegistered = false;

    /**
     * 初始化方法，服务端建议在Application::attachBaseContext中调用；
     * <p>客户端随意
     * @param context
     */
    public static void init(Context context) {
        BusInternal.INS.init(context);
    }

    /**
     * 销毁方法，不再使用总线时可以调用<p>
     * 如果覆盖整个APP的生命周期，建议不调用
     */
    public static void release() {
        BusInternal.INS.destroy();
        SubscriberCacheManager.INS.unSubscribeAll();
    }

    /**
     * 注册一个事件的同步调用监听服务
     * @param key 事件名称
     * @param binderServer
     */
    public void registerServer(String key, RequestListener binderServer) {
        BusInternal.INS.registerServer(key, binderServer);
    }

    /**
     * 注销一个同步响应监听
     * @param key
     */
    public void unRegisterServer(String key) {
        BusInternal.INS.unRegisterServer(key);
    }

    /**
     * 订阅一个事件分发的监听器
     * @param key 事件名称
     * @param eventListener
     */
    public void subscribe(String key, EventListener eventListener) {
        if (!isClientRegistered) {
            BusInternal.INS.registerClient();
            isClientRegistered = true;
        }

        SubscriberCacheManager.INS.subscribe(key, eventListener);
    }

    /**
     * 注销一个事件分发监听器
     * @param key 事件名称
     * @param eventListener
     */
    public void unSubscribe(String key, EventListener eventListener) {
        SubscriberCacheManager.INS.unSubscribe(key, eventListener);
    }

    /**
     * 分发一个事件
     * @param key 事件名称
     * @param event 事件数据
     */
    public void post(String key, Bundle event) {
        BusInternal.INS.post(key, event);
    }

    /**
     * 获取一个事件数据
     * @param key 事件名称
     * @param params 参数
     * @return 数据
     */
    public Bundle get(String key, Bundle params) {
        return BusInternal.INS.get(key, params);
    }

    /**
     * 设置一个事件数据
     * @param key 事件名称
     * @param params 参数
     */
    public void set(String key, Bundle params) {
        BusInternal.INS.set(key, params);
    }
}
