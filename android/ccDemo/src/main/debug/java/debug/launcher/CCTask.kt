package debug.launcher

import com.billy.cc.core.component.CC
import com.billy.cc.core.component.IDynamicComponent
import com.elvishew.xlog.XLog
import com.hele.base.launcher.task.Task
import com.hele.ccdemo.BuildConfig
import java.util.ServiceLoader

class CCTask : Task() {
    override fun isRunOnMainThread(): Boolean = false

    override fun run() {
        initCC()
    }

    private fun initCC() {
        CC.enableDebug(BuildConfig.DEBUG)
        CC.enableVerboseLog(BuildConfig.DEBUG)
        CC.enableRemoteCC(BuildConfig.DEBUG)

        // 组件注册 -- 可以用autoService 来进行自动注册 -- 使用反射，比asm在性能上要差一点点，但是兼容性比asm要好很多
//        CC.registerComponent(CcDemoComponent())
        ServiceLoader.load(IDynamicComponent::class.java, IDynamicComponent::class.java.classLoader)
            .forEach {
                XLog.d("register $it")
                CC.registerComponent(it)
            }
    }
}