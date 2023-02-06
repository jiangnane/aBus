// IBinderBridge.aidl
package com.jiangnane.abus.server;

// Declare any non-default types here with import statements
interface IBinderBridge {
    void addClientBinder(String name, IBinder binder);
    void removeClientBinder(String name);
    void post(String key, in Bundle extra);

    void addServerBinder(String name, IBinder binder);
    void removeServerBinder(String name);
    Bundle call(int method, String key, in Bundle params);
}
