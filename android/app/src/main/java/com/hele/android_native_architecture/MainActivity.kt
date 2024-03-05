package com.hele.android_native_architecture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import coil.compose.rememberAsyncImagePainter
import com.billy.cc.core.component.CC
import com.elvishew.xlog.XLog
import com.hele.android_native_architecture.plugin.TestPlugin
import com.hele.android_native_architecture.plugin.TestStaticJavaPlugin
import com.hele.android_native_architecture.plugin.TestStaticPlugin
import com.hele.android_native_architecture.ui.theme.Android_native_architectureTheme
import com.hele.android_native_architecture.viewmodel.MainUIStateData
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
    private val mainViewModel: MainViewModel by inject()

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
                    val mainUIState = mainViewModel.mainUIStateLiveData.observeAsState()
                    val mainUIStateData by remember {
                        mainUIState
                    }
                    Greeting2(mainUIStateData ?: MainUIStateData())
                }
            }
        }
        XLog.d("shareViewModel = ${shareViewModel.hashCode()}")
        testPlugin()

        testCC()

        mainViewModel.apply {
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
                }.onCompletion {
                    XLog.d("flow complete")
                }.collect {
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

    @Composable
    fun testCompose() {
        Text(text = "test compose")
    }
}

@Composable
fun Greeting2(mainUIState: MainUIStateData) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue, RoundedCornerShape(8.dp))
    ) {
        val (text1, button1, image1) = createRefs()
        Button(onClick = {}, modifier = Modifier
            .constrainAs(button1) {
                top.linkTo(parent.top, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
            .defaultMinSize(200.dp)) {
            Text(text = mainUIState.name)
        }

        Text(text = "show dialog ${mainUIState.text}", modifier = Modifier
            .constrainAs(text1) {
                top.linkTo(button1.bottom, 8.dp)
                start.linkTo(button1.start)
            }
            .background(Color.Red)
            .padding(8.dp)
            .clickable {
                mainUIState.showMyDialog()
            }
        )

        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "",
            modifier = Modifier
                .constrainAs(image1) {
                    top.linkTo(text1.bottom, 8.dp)
                    start.linkTo(button1.end)
                }
                .width(60.dp)
        )
    }

    if (mainUIState.showDialog) {
        myDialog(title = "My Dialog Title", content = "My Dialog Content") {
            mainUIState.dialogConfirm()
        }
    }
}

@Composable
fun Greeting(mainUIState: MainUIStateData) {
    Column(modifier = Modifier.padding(8.dp)) {
        val focusManager = LocalFocusManager.current
        Text(
            text = "Hello ${mainUIState.name}! 丝丝xxxxxxxxx\nxxx\nx",
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .background(Color.Blue, shape = RoundedCornerShape(8.dp))
                .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 8.dp)
                .defaultMinSize(minHeight = 100.dp),
            color = Color.Red,
            fontSize = 18.sp,
            fontWeight = Bold,
            textAlign = TextAlign.Center
        )

        Box(contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(start = 8.dp, top = 8.dp)
                .fillMaxWidth()
                .defaultMinSize(minHeight = 100.dp)
                .height(100.dp)
                .clickable {

                }) {
            Image(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxWidth()
                    .wrapContentHeight(),
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentScale = ContentScale.Crop,
                contentDescription = ""
            )
            Text(
                text = "Hello ${mainUIState.text}",
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .clickable {
                        mainUIState.textClick()
                    },
                color = Color.Blue,
                fontSize = 20.sp,
                fontFamily = FontFamily.Default,
                textAlign = TextAlign.Center,
                fontWeight = Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = "",
            modifier = Modifier
                .clip(CircleShape)
                .background(colorResource(id = R.color.purple_200))
                .size(width = 80.dp, height = 80.dp)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                .clickable {
                    val success = focusManager.clearFocus(true)
                    XLog.d("free focus result: $success")
                }
        )
        EditText()
        EditText()
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = rememberAsyncImagePainter(
                model = "https://th.bing.com/th/id/R.1395d1b17397018e6916784c283a14f2?rik=bmfmSW7odc2D1A&pid=ImgRaw&r=0",
                placeholder = painterResource(id = R.drawable.ic_launcher_background),
                error = painterResource(id = R.drawable.ic_launcher_foreground),

                ),
            contentDescription = "remote image",
            contentScale = ContentScale.Crop, //FillBounds,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .fillMaxWidth()
//                .width((375 / 2).dp)
                .height(100.dp)
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditText() {
    var text11: String by remember {
        mutableStateOf("edit text")
    }

    TextField(
        value = text11,
        onValueChange = {
            text11 = it
        },
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .defaultMinSize(minHeight = 100.dp),
        textStyle = TextStyle(
            color = Color.Red, fontSize = 28.sp, fontWeight = Bold,
            textAlign = TextAlign.Center
        ),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor = Color.Red
        ),
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Android_native_architectureTheme {
        Greeting(MainUIStateData("Android Compose"))
    }
}

@Preview(showBackground = true)
@Composable
fun Greeting2Preview() {
    Android_native_architectureTheme {
        Greeting2(MainUIStateData("Android Compose", showDialog = true))
    }
}

@Composable
fun myDialog(title: String, content: String, onConfirm: () -> Unit) {
    Dialog(
        onDismissRequest = { }, properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = false
        )
    ) {
        ConstraintLayout(
            modifier = Modifier
                .background(
                    Color.Blue, RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
        ) {
            val (tvTitle, tvContent, btnConfirm) = createRefs()
            Text(
                text = title, modifier = Modifier.constrainAs(tvTitle) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                color = Color.White
            )
            Text(text = content, modifier = Modifier.constrainAs(tvContent) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(tvTitle.bottom, 8.dp)
            }, color = Color.Red)
            Button(onClick = { onConfirm() }, modifier = Modifier.constrainAs(btnConfirm) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(tvContent.bottom, 8.dp)

            }) {
                Text(text = "Confirm")
            }
        }
    }
}

@Preview
@Composable
fun myDialogPreview() {
    myDialog(title = "Dialog Title", content = "Dialog Content") {

    }
}

val myFun = @Composable {

}

fun myFun2(myParams: @Composable () -> Unit) {
//        myParams.invoke()
}