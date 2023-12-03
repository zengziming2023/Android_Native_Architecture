package com.hele.webview.commands

abstract class AbsLocalCommandHandler : ICommandHandler {

    override fun isHandleByMainProcess(): Boolean = false
}