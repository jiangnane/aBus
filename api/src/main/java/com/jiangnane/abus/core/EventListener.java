package com.jiangnane.abus.core;

import android.os.Bundle;

/**
 * 事件订阅后的回调接口
 * <p>
 * Created by hanwei on 22/10/8.
 */
public interface EventListener {

    /**
     * 事件回调
     * @param eventName 事件名称
     * @param event 事件参数
     */
    void onEvent(String eventName, Bundle event);
}
