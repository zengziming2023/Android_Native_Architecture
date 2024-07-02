package com.hele.android_native_architecture.di

import com.hele.android_native_architecture.viewmodel.MainViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * Module 用于提供对象
 */
@Module
class ViewModelModule {

    @Singleton
    @Provides
    fun provideMainViewModel(): MainViewModel {
        return MainViewModel()
    }
}