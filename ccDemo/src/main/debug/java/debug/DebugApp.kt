package debug

import com.hele.base.BaseApplication
import com.hele.base.launcher.task.Task
import debug.launcher.CCTask

class DebugApp : BaseApplication() {

    override fun getTasks(): List<Task>? {
        return listOf(CCTask())
    }
}