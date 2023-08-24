package com.hele.android_native_architecture

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.billy.cc.core.component.CC
import com.billy.cc.core.component.CCResult
import com.hele.android_native_architecture.cc.ComponentTest
import com.hele.android_native_architecture.ui.theme.Android_native_architectureTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Android_native_architectureTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }

        testCC()
    }

    private fun testCC() {
        // 同步调用
        val result = CC.obtainBuilder("ComponentTest")
            .setActionName("toast")
            .build().call()

        // 异步调用，不需要回调
        val callId = CC.obtainBuilder("ComponentTest")
            .setActionName("toast")
//            .addParam()   // 入参
            .build().callAsync()

        // 异步调用，需要回调
        val callId2 = CC.obtainBuilder("ComponentTest")
            .setActionName("toast")
//            .addParam()   // 入参
            .build().callAsync { cc, result ->
                // 回调在子线程中
                Log.d("", "$result")
            }

        // 异步调用，主线程回调
        val callId3 = CC.obtainBuilder("ComponentTest")
            .setActionName("toast")
//            .addParams()
            .build().callAsyncCallbackOnMainThread { cc, result ->
                Log.d("", "$result")
            }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Android_native_architectureTheme {
        Greeting("Android")
    }
}