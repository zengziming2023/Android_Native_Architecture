package com.hele.android_native_architecture.cc

import com.billy.cc.core.component.CC
import com.billy.cc.core.component.CCResult
import com.billy.cc.core.component.IDynamicComponent
import com.elvishew.xlog.XLog
import com.google.auto.service.AutoService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AutoService(IDynamicComponent::class)
class ComponentTest : IDynamicComponent {
    override fun getName(): String = "ComponentTest"  // 组件名具有唯一性

    override fun onCall(cc: CC): Boolean {
        return when (cc.actionName) {
            "toast" -> {
                XLog.d("component toast test...params = ${cc.params}")
                CC.sendCCResult(cc.callId, CCResult.success())
                false // false: 同步； true: 异步结果
            }

            "toastAsync" -> {
                XLog.d("component toastAsync test...params = ${cc.params}")
                CC.sendCCResult(cc.callId, CCResult.success())
                true
            }

            else -> {
                CC.sendCCResult(cc.callId, CCResult.errorUnsupportedActionName())
                false
            }
        }
    }
}