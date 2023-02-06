package com.jiangnane.abus.core;


import com.jiangnane.abus.BuildConfig;

/**
 * 常量定义
 * <p>
 * Created by hanwei on 22/10/8.
 */
public class Constant {

    /**
     * Binder Provider的路径名
     */
    public static final String SERVICE_CP_AUTH = BuildConfig.authoritiesPrefix + ".Binder";

    /**
     * Call默认方法名
     */
    public static final String CALL_METHOD = "@";

    /**
     * Binder通道标记符(没有此标记的连接忽略)
     */
    public static final String BINDER_KEY = BuildConfig.binderKey;
    public static final String POST_DUR_MEASURE = "___time_start___";

    /**
     * C端通道名Key前缀
     */
    public static final String BINDER_CLIENT_PREFIX = "C.";
    public static final String BINDER_SERVER_PREFIX = "S.";
}
