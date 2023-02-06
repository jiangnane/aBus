package com.jiangnane.abus.utils.compat;

import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Helper for accessing features in {@link Bundle}.
 */
public final class BundleCompat {

    static class BundleCompatBaseImpl {
        private static final String TAG = "BundleCompatBaseImpl";

        private static Method sGetIBinderMethod;
        private static boolean sGetIBinderMethodFetched;

        private static Method sPutIBinderMethod;
        private static boolean sPutIBinderMethodFetched;

        public static IBinder getBinder(Bundle bundle, String key) {
            if (!sGetIBinderMethodFetched) {
                try {
                    sGetIBinderMethod = Bundle.class.getMethod("getIBinder", String.class);
                    sGetIBinderMethod.setAccessible(true);
                } catch (NoSuchMethodException e) {
                    Log.i(TAG, "Failed to retrieve getIBinder method", e);
                }
                sGetIBinderMethodFetched = true;
            }

            if (sGetIBinderMethod != null) {
                try {
                    return (IBinder) sGetIBinderMethod.invoke(bundle, key);
                } catch (InvocationTargetException | IllegalAccessException
                        | IllegalArgumentException e) {
                    Log.i(TAG, "Failed to invoke getIBinder via reflection", e);
                    sGetIBinderMethod = null;
                }
            }
            return null;
        }

        public static void putBinder(Bundle bundle, String key, IBinder binder) {
            if (!sPutIBinderMethodFetched) {
                try {
                    sPutIBinderMethod =
                            Bundle.class.getMethod("putIBinder", String.class, IBinder.class);
                    sPutIBinderMethod.setAccessible(true);
                } catch (NoSuchMethodException e) {
                    Log.i(TAG, "Failed to retrieve putIBinder method", e);
                }
                sPutIBinderMethodFetched = true;
            }

            if (sPutIBinderMethod != null) {
                try {
                    sPutIBinderMethod.invoke(bundle, key, binder);
                } catch (InvocationTargetException | IllegalAccessException
                        | IllegalArgumentException e) {
                    Log.i(TAG, "Failed to invoke putIBinder via reflection", e);
                    sPutIBinderMethod = null;
                }
            }
        }
    }

    private BundleCompat() {}

    /**
     * A convenience method to handle getting an {@link IBinder} inside a {@link Bundle} for all
     * Android versions.
     * @param bundle The bundle to get the {@link IBinder}.
     * @param key    The key to use while getting the {@link IBinder}.
     * @return       The {@link IBinder} that was obtained.
     */
    public static IBinder getBinder(Bundle bundle, String key) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            return bundle.getBinder(key);
        } else {
            return BundleCompatBaseImpl.getBinder(bundle, key);
        }
    }

    /**
     * A convenience method to handle putting an {@link IBinder} inside a {@link Bundle} for all
     * Android versions.
     * @param bundle The bundle to insert the {@link IBinder}.
     * @param key    The key to use while putting the {@link IBinder}.
     * @param binder The {@link IBinder} to put.
     */
    public static void putBinder(Bundle bundle, String key, IBinder binder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bundle.putBinder(key, binder);
        } else {
            BundleCompatBaseImpl.putBinder(bundle, key, binder);
        }
    }
}
