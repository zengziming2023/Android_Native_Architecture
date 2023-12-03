package com.hele.webview.h5process

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.hele.base.extensions.applicationContext
import com.hele.webview.IH5ToMain
import com.hele.webview.mainprocess.MainCommandService

internal object H5ToMainAidlManager : ServiceConnection {
    private var iH5ToMain: IH5ToMain? = null

    fun getH5ToMain() = iH5ToMain
    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        iH5ToMain = IH5ToMain.Stub.asInterface(service)
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        iH5ToMain = null
        initAidlConnect()
    }

    override fun onBindingDied(name: ComponentName?) {
        super.onBindingDied(name)
        iH5ToMain = null
        initAidlConnect()
    }

    /**
     * H5 process
     */
    fun initAidlConnect() {
        applicationContext().apply {
            bindService(
                Intent(this, MainCommandService::class.java),
                this@H5ToMainAidlManager,
                Context.BIND_AUTO_CREATE
            )
        }
    }
}