// IH5ToMain.aidl
package com.hele.webview;

// Declare any non-default types here with import statements
import com.hele.webview.ICallbackFromMainToH5;

interface IH5ToMain {

    void handleH5Command(String commandName, String jsonParams, in ICallbackFromMainToH5 callback);
}