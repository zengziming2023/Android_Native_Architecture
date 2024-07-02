package com.hele.base.launcher.dispatcher

import android.os.Looper
import com.hele.base.launcher.runable.TaskRunnable
import com.hele.base.launcher.task.Task
import com.hele.base.launcher.utils.TaskLogUtil
import com.hele.base.launcher.utils.TaskSortUtil
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * 启动任务调度类(单例模式)
 *      1、添加必要参数(主线程判断，是否显示日志，等待总时长)
 *      2、添加任务到列表
 *      3、开启任务
 *          3.1、前期判断，必须在主线程执行
 *          3.2、任务列表拓扑排序
 *          3.3、拆分线程任务
 *          3.4、分线程执行任务
 *      4、任务等待
 *      5、其他调用
 *          通知子任务，父任务已完成
 *          标记任务完成
 */
object TaskDispatcher {
    //等待总时长
    var mAllTaskWaitTimeOut: Long = 10000

    //用于等待需要等待的任务完成
    private var mCountDownLatch: CountDownLatch? = null

    //需要等待的数量
    private val mNeedWaitCount = AtomicInteger()

    //所有启动任务任务
    private val M_START_TASK_LIST: ArrayList<Task> = ArrayList()

    //存放每个Task  （key= Class < ? extends AppStartTask>）
    private val M_TASK_MAP: HashMap<Class<out Task>, Task> = HashMap()

    //每个Task的孩子 （key= Class < ? extends AppStartTask>）
    private val M_TASK_CHILD_MAP: HashMap<Class<out Task?>, HashSet<Class<out Task>>> =
        HashMap()

    //拓扑排序后的主线程的任务
    private val M_SORT_MAIN_THREAD_TASK_LIST: ArrayList<Task> = ArrayList()

    //拓扑排序后的子线程的任务
    private val M_SORT_THREAD_POOL_TASK_LIST: ArrayList<Task> = ArrayList()

    //记录启动总时间
    private var mStartTime: Long = 0L

    fun setAllTaskWaitTimeOut(timeOut: Long): TaskDispatcher {
        mAllTaskWaitTimeOut = timeOut
        return this
    }

    /**
     * 添加启动任务类
     */
    fun addTask(task: Task?): TaskDispatcher {
        task?.let {
            //加入列表
            M_START_TASK_LIST.add(task)
            //如果需要等待执行，等待数量加1
            if (task.needWait()) {
                mNeedWaitCount.getAndIncrement()
            }
        } ?: throw RuntimeException("addAppStartTask() 传入的appStartTask为null")
        return this
    }

    /**
     * 开启任务
     */
    fun start(): TaskDispatcher {
        //前期判断准备
        if (Looper.getMainLooper() != Looper.myLooper()) throw RuntimeException("start方法必须在主线程调用")
        //开始点(start和await连着用才有效)
        mStartTime = System.currentTimeMillis()
        //获取拓扑排序后的列表
        val sortAppStartTask =
            TaskSortUtil.sortAppStartTask(M_START_TASK_LIST, M_TASK_MAP, M_TASK_CHILD_MAP)
        //打印列表日志
        printSortTask(sortAppStartTask)
        //拆分主线程任务和子线程任务
        initRealSortTask(sortAppStartTask)
        //创建计数器
        mCountDownLatch = CountDownLatch(mNeedWaitCount.get())
        //对应线程执行任务
        dispatchAppStartTask()
        return this
    }

    //等待，阻塞主线程
    fun await() {
        try {
            mCountDownLatch?.await(mAllTaskWaitTimeOut, TimeUnit.MILLISECONDS)
                ?: throw java.lang.RuntimeException("在调用await()之前，必须先调用start()")
            TaskLogUtil.showLog("启动耗时：${System.currentTimeMillis() - mStartTime}")
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    /**
     * 输出排好序的Task
     */
    private fun printSortTask(sortTask: ArrayList<Task>) {
        val sb = StringBuilder()
        sb.append("当前所有任务排好的顺序为：")
        sortTask.forEachIndexed { i, task ->
            val taskName: String = task::class.java.simpleName
            if (i == 0) {
                sb.append(taskName)
            } else {
                sb.append("---＞")
                sb.append(taskName)
            }
        }
        TaskLogUtil.showLog(sb.toString())
    }

    /**
     * 分别处理主线程和子线程的任务
     */
    private fun initRealSortTask(sortTask: ArrayList<Task>) {
        M_SORT_MAIN_THREAD_TASK_LIST.clear()
        M_SORT_THREAD_POOL_TASK_LIST.clear()
        for (appStartTask in sortTask) {
            if (appStartTask.isRunOnMainThread()) {
                M_SORT_MAIN_THREAD_TASK_LIST.add(appStartTask)
            } else {
                M_SORT_THREAD_POOL_TASK_LIST.add(appStartTask)
            }
        }
    }

    /**
     * 分线程执行任务
     */
    private fun dispatchAppStartTask() {
        M_SORT_THREAD_POOL_TASK_LIST.forEach { threadTask ->
            threadTask.runOnExecutor().execute(TaskRunnable(threadTask, this))

        }
        M_SORT_MAIN_THREAD_TASK_LIST.forEach { mainTask ->
            TaskRunnable(mainTask, this).run()
        }
    }

    /**
     * 通知子任务，父任务完成了
     */
    fun setNotifyChildren(task: Task) {
        M_TASK_CHILD_MAP[task::class.java]?.forEach { cls ->
            M_TASK_MAP[cls]?.notifyDependsTaskFinish()
        }
    }

    /**
     * 标识需要等待的任务完成
     */
    fun markTaskFinish(task: Task) {
        TaskLogUtil.showLog("任务完成了：" + task::class.java.simpleName)
        //如果是需要等待的任务
        if (ifNeedWait(task)) {
            //等待数量-1
            mCountDownLatch?.countDown()
            mNeedWaitCount.getAndDecrement()
        }
    }

    /**
     * 是否需要等待，主线程的任务本来就是阻塞的，所以不用管
     */
    private fun ifNeedWait(task: Task): Boolean {
        return !task.isRunOnMainThread() && task.needWait()
    }
}

