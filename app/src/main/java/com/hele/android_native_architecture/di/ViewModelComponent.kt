package com.hele.android_native_architecture.di

import com.hele.android_native_architecture.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ViewModelModule::class])
interface ViewModelComponent {
    fun injectMainActivity(mainActivity: MainActivity)
}