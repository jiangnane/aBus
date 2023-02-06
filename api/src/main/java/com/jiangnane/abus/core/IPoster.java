package com.jiangnane.abus.core;

import android.os.Bundle;

/**
 * 用于通信方法Post的接口定义
 * <p>
 * Created by hanwei on 22/10/8.
 * <p>
 * 通过实现不同的Poster来使用不同的通信机制
 */
public interface IPoster {
    void post(String key, Bundle bundle);
}
