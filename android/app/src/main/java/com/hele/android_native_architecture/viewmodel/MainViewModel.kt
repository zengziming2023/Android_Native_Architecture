package com.hele.android_native_architecture.viewmodel

import com.elvishew.xlog.XLog
import com.hele.base.viewmodel.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class MainViewModel : BaseViewModel() {

    fun testKoin() {
        XLog.d("testKoin this.hashCode = ${this.hashCode()}")
    }

    fun testFlow1(): Flow<Int> = flowOf(
        1, 2, 3, 4, 5
    )


    fun testFlow2(): Flow<Int> = flow {
        XLog.d("testFlow2 start")
        emit(11)
        delay(300)
        emit(12)
        delay(300)
        emit(13)
        delay(300)
        XLog.d("after delay testFlow2")
        emit(14)
        XLog.d("testFlow2 end")
    }

    suspend fun testFlow3(): Int {
        delay(200)
        return 3
    }

    fun testFlow4() = ::testFlow3.asFlow()

}