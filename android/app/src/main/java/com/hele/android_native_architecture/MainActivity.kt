package com.hele.android_native_architecture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.billy.cc.core.component.CC
import com.elvishew.xlog.XLog
import com.hele.android_native_architecture.plugin.TestPlugin
import com.hele.android_native_architecture.plugin.TestStaticJavaPlugin
import com.hele.android_native_architecture.plugin.TestStaticPlugin
import com.hele.android_native_architecture.ui.theme.Android_native_architectureTheme
import com.hele.android_native_architecture.viewmodel.MainViewModel
import com.hele.android_native_architecture.viewmodel.ShareViewModel
import com.hele.android_native_architecture.viewmodel.globalSharedViewModel
import com.hele.base.annotation.TraceMethod
import com.hele.base.utils.LoginUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class MainActivity : ComponentActivity() {
    private val mainViewModule: MainViewModel by inject()

    private val shareViewModel by lazy {
        globalSharedViewModel<ShareViewModel>()
    }

    @TraceMethod
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Android_native_architectureTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
        XLog.d("shareViewModel = ${shareViewModel.hashCode()}")
        testPlugin()

        testCC()

        mainViewModule.apply {
            testKoin()

            lifecycleScope.launch(Dispatchers.IO) {
                XLog.v("start flow 1.")
//                testFlow1().collect {
//                    XLog.d("test flow 1, collect value = $it")
//                }
//                XLog.v("start flow 2.")
//                testFlow2().collect {
//                    XLog.d("test flow 2, collect value = $it")
//                }
//
//                testFlow4().collect {
//
//                }
//                flow {
//                    emit(testFlow3())
//                }.catch {
//
//                }.collect {
//
//                }
//
//                ::testFlow3.asFlow().collect {
//                    XLog.d("as flow test flow3 : $it")
//                }

                // transform start
//                testFlow1().filter {
//                    it < 3
//                }.map {
//                    "after map $it"
//                }.flatMapConcat {
//                    flow {
//                        if (it.contains("1")) {
//                            delay(300)
//                        }
//                        emit("flat map $it")
//                    }
//                }
//                    .flowOn(Dispatchers.Default)
//                    .collect {
//                        XLog.d(it)
//                    }

//                merge(testFlow1(), testFlow2()).collect {
//                    XLog.d("merge $it")
//                }

                testFlow1().zip(testFlow2()) { i: Int, j: Int ->
                    "$i, $j"
                }.catch {
                    XLog.d("zip catch: $it")
                }.onStart {
                    XLog.d("flow start")
                }
                    .onCompletion {
                        XLog.d("flow complete")
                    }
                    .collect {
                        XLog.d("zip $it")
                    }
                // transform end

            }
        }
    }

    private fun testPlugin() {
        TestPlugin.test()

        TestPlugin.testLoginRequest()
        LoginUtil.login()
        TestPlugin.testLoginRequest()
        TestPlugin.testAfterLogin("has login: go go go..")
        TestPlugin.testAfterLogin("has login: go go go..", 200)

        TestStaticPlugin.testStatic("test static..", 200)
        TestStaticJavaPlugin.testJavaStatic("test java static..", 200)
        TestPlugin.testThread()
    }

    private fun testCC() {
        // 同步调用
        val result = CC.obtainBuilder("ComponentTest").setActionName("toast").build().call()

        // 异步调用，不需要回调
        val callId = CC.obtainBuilder("ComponentTest").setActionName("toast")
//            .addParam()   // 入参
            .build().callAsync()

        // 异步调用，需要回调
        val callId2 = CC.obtainBuilder("ComponentTest").setActionName("toast")
//            .addParam()   // 入参
            .build().callAsync { cc, result ->
                // 回调在子线程中
                XLog.d("", "$result")
            }

        // 异步调用，主线程回调
        val callId3 = CC.obtainBuilder("ComponentTest").setActionName("toast")
//            .addParams()
            .build().callAsyncCallbackOnMainThread { cc, result ->
                XLog.d("", "$result")
            }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column(modifier = Modifier.padding(8.dp)) {
        var text by remember {
            mutableStateOf("2222")
        }

        Text(
            text = "Hello $name!",
            modifier = Modifier
                .background(Color.Blue, shape = RoundedCornerShape(8.dp))
                .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp),
            color = Color.Red,
            fontSize = 18.sp,
            fontWeight = Bold
        )
        Text(text = "Hello $text",
            modifier = Modifier.clickable {
                text = if (text == "2222") {
                    "3333"
                } else {
                    "2222"
                }
            })
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "",
            modifier = Modifier
                .clip(CircleShape)
                .background(colorResource(id = R.color.purple_200))
                .size(width = 80.dp, height = 80.dp)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
    }

}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Android_native_architectureTheme {
        Greeting("Android Compose")
    }
}