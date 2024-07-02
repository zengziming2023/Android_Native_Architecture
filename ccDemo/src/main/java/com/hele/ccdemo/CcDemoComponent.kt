package com.hele.ccdemo

import com.billy.cc.core.component.CC
import com.billy.cc.core.component.CCResult
import com.billy.cc.core.component.IDynamicComponent
import com.elvishew.xlog.XLog
import com.google.auto.service.AutoService

@AutoService(IDynamicComponent::class)
internal class CcDemoComponent : IDynamicComponent {
    override fun getName(): String {
        return "CcDemoComponent"
    }

    override fun onCall(cc: CC?): Boolean {
        if (cc == null) {
            return false
        }
        when (cc.actionName) {
            "toast" -> {
                XLog.d("receive action: toast, ${cc.params}")
                CC.sendCCResult(cc.callId, CCResult.success())
            }
        }
        return false
    }
}