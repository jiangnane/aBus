package com.jiangnane.abus.core.impl;

import android.os.Bundle;

import com.jiangnane.abus.core.IPoster;
import com.jiangnane.abus.core.server.BinderManager;

/**
 * 使用ProviderBinder实现Poster
 * <p>
 * Created by hanwei on 22/10/8.
 */
public class DefaultPoster implements IPoster {
    @Override
    public void post(String key, Bundle bundle) {
        BinderManager.INS.post(key,bundle);
    }
}
