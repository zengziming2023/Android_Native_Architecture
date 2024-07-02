package com.hele.base.launcher.runable

import android.os.Process
import com.hele.base.launcher.dispatcher.TaskDispatcher
import com.hele.base.launcher.task.Task

/**
 * 任务任务类
 *  执行流程：
 *      1、设置优先级
 *      2、设置任务等待
 *      3、执行任务内容
 *      4、通知子任务完成
 *      5、标记任务完成
 */
class TaskRunnable(
    private val task: Task,
    private val dispatcher: TaskDispatcher? = null
) :
    Runnable {
    override fun run() {
        //设置优先级
        Process.setThreadPriority(task.priority())
        //任务等待
        task.waitToNotify()
        //执行内容
        task.run()
        //通知子任务完成
        dispatcher?.setNotifyChildren(task)
        //标记任务结束
        dispatcher?.markTaskFinish(task)
    }
}