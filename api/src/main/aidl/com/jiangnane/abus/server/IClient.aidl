// IClient.aidl
package com.jiangnane.abus.server;

// Declare any non-default types here with import statements

interface IClient {
    void onEventReceive(String key, in Bundle event);
}
