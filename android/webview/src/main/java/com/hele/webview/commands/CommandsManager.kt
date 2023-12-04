package com.hele.webview.commands

import android.webkit.WebView
import com.hele.webview.ICallbackFromMainToH5
import com.hele.webview.h5process.H5ToMainAidlManager
import java.lang.ref.WeakReference
import java.util.ServiceLoader

internal object CommandsManager {
    private val commandsMap by lazy {
        mutableMapOf<String, ICommandHandler>().apply {
            val serviceLoader = ServiceLoader.load(
                ICommandHandler::class.java,
                ICommandHandler::class.java.classLoader
            )
            for (command in serviceLoader) {
                put(command.commandName(), command)
            }
        }
    }

    fun getAllCommandsMap(): Map<String, ICommandHandler> {
        return commandsMap
    }

    /**
     * The only entry point to handle h5 messages.
     */
    fun executeCommand(
        commandName: String,
        jsonParam: String?,
        callbackId: String?,
        webViewRef: WeakReference<WebView>
    ) {
        commandsMap[commandName]?.let { handler ->
            val callback = generalCallback(callbackId, webViewRef)
            if (handler.isHandleByMainProcess()) {
                H5ToMainAidlManager.getH5ToMain()?.handleH5Command(commandName, jsonParam, callback)
            } else {
                handler.handle(jsonParam, callback)
            }
        }
    }

    private fun generalCallback(
        callbackId: String?,
        webViewRef: WeakReference<WebView>
    ): ICallbackFromMainToH5? {
        return callbackId?.let {
            object : ICallbackFromMainToH5.Stub() {
                override fun onResult(response: String?, closeH5Container: Boolean) {
                    // TODO: send result to js.
                }

                override fun getCallbackId(): String = it

                override fun getH5ContainerTag(): Int = webViewRef.get()?.hashCode() ?: -1

            }
        }
    }
}