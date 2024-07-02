package com.hele.webview.commands

import com.hele.webview.ICallbackFromMainToH5

interface ICommandHandler {
    fun commandName(): String
    fun isHandleByMainProcess(): Boolean
    fun handle(jsonParams: String?, callback: ICallbackFromMainToH5?)
}