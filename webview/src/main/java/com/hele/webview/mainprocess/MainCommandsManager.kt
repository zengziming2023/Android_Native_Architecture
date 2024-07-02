package com.hele.webview.mainprocess

import com.elvishew.xlog.XLog
import com.hele.webview.ICallbackFromMainToH5
import com.hele.webview.IH5ToMain
import com.hele.webview.commands.CommandsManager

internal object MainCommandsManager : IH5ToMain.Stub() {
    override fun handleH5Command(
        commandName: String?,
        jsonParams: String?,
        callback: ICallbackFromMainToH5?
    ) {
        XLog.d("handleH5Command $commandName, $jsonParams, $callback")
        CommandsManager.getAllCommandsMap()[commandName]?.handle(
            jsonParams, callback
        )
    }
}