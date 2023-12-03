package com.hele.webview.mainprocess

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * h5 process activity use aidl to start this service and get a main process IBinder
 */
internal class MainCommandService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        return MainCommandsManager     // this IBinder return to h5 process, and use to
    }
}