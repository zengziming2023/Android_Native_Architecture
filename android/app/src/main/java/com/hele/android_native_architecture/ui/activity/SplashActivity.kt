package com.hele.android_native_architecture.ui.activity

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.content.Intent
import com.elvishew.xlog.XLog
import com.hele.android_native_architecture.MainActivity
import com.hele.android_native_architecture.databinding.ActivitySplashBinding
import com.hele.base.ui.BaseActivity
import com.hele.base.viewmodel.BaseViewModel

class SplashActivity : BaseActivity<ActivitySplashBinding, BaseViewModel>() {
    override fun setUpView() {
        mViewBinding.apply {
            lottieView.apply {
                addAnimatorListener(object : AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                        XLog.d("onAnimationStart")
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        XLog.d("onAnimationEnd")
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        XLog.d("onAnimationCancel")
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                        XLog.d("onAnimationRepeat")
                    }
                })

                setOnClickListener {
                    cancelAnimation()

                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
            }
        }
    }

    override fun applyViewModel() {
    }
}