package com.anhhoang.aws.core.coroutines.di

import com.anhhoang.aws.core.coroutines.executors.BlockingContext
import com.anhhoang.aws.core.coroutines.executors.LightweightContext
import com.anhhoang.aws.core.coroutines.executors.MainContext
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.Dispatchers

/**
 * Module to provide the app with appropriate coroutine contexts for each operation. Helps
 * with testability and decouple the direct usage.
 */
@InstallIn(SingletonComponent::class)
@Module
internal object CoroutinesModule {
    @MainContext
    @Provides
    @Singleton
    fun provideMainContext(): CoroutineContext = Dispatchers.Main

    @BlockingContext
    @Provides
    @Singleton
    fun provideBlockingContext(): CoroutineContext = Dispatchers.IO

    @LightweightContext
    @Provides
    @Singleton
    fun provideLightweightContext(): CoroutineContext = Dispatchers.Default
}
