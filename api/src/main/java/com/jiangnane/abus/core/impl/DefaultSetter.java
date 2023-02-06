package com.jiangnane.abus.core.impl;

import android.os.Bundle;

import com.jiangnane.abus.core.ISetter;
import com.jiangnane.abus.core.server.BinderManager;

/**
 * 使用ProviderBinder实现Setter
 * <p>
 * Created by hanwei on 22/10/8.
 */
public class DefaultSetter implements ISetter {
    @Override
    public void set(String key, Bundle bundle) {
        BinderManager.INS.set(key, bundle);
    }
}
