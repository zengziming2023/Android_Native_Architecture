// ICallbackFromMainToH5.aidl
package com.hele.webview;

// Declare any non-default types here with import statements

interface ICallbackFromMainToH5 {

    void onResult(String response, boolean closeH5Container);

    String getCallbackId();

    int getH5ContainerTag();

}