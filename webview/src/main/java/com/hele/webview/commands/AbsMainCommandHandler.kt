package com.hele.webview.commands

abstract class AbsMainCommandHandler : ICommandHandler {
    override fun isHandleByMainProcess(): Boolean = true
}