package debug

import com.billy.cc.core.component.CC
import com.elvishew.xlog.XLog
import com.hele.base.ui.BaseActivity
import com.hele.base.viewmodel.BaseViewModel
import com.hele.ccdemo.databinding.CcdemoDebugActivityBinding

class DebugActivity : BaseActivity<CcdemoDebugActivityBinding, BaseViewModel>() {
    override fun setUpView() {
        mViewBinding.apply {
            tvTestDemo.setOnClickListener {
                // test local
                CC.obtainBuilder("CcDemoComponent")
                    .setActionName("toast")
                    .addParam("key1", "value1")
                    .build()
                    .callAsync { cc, result ->
                        XLog.d("cc demo result = $result")
                    }

                // test remote
                CC.obtainBuilder("ComponentTest")
                    .setActionName("toast")
                    .addParam("key1", "value1")
                    .build()
                    .callAsync { cc, result ->
                        XLog.d("cc demo remote result = $result")
                    }
            }
        }
    }

    override fun applyViewModel() {
    }
}