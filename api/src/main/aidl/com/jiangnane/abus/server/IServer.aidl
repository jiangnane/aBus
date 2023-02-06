// IServer.aidl
package com.jiangnane.abus.server;

// Declare any non-default types here with import statements

interface IServer {
    Bundle onRequest(int method, String key, in Bundle params);
}
