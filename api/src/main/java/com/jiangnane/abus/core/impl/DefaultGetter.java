package com.jiangnane.abus.core.impl;

import android.os.Bundle;

import com.jiangnane.abus.core.IGetter;
import com.jiangnane.abus.core.server.BinderManager;

/**
 * 使用ProviderBinder实现Getter
 * <p>
 * Created by hanwei on 22/10/8.
 */
public class DefaultGetter implements IGetter {
    @Override
    public Bundle get(String key, Bundle params) {
        return BinderManager.INS.get(key, params);
    }
}
