package com.anhhoang.aws.feature.s3reader.impl.local.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.anhhoang.aws.core.coroutines.executors.BlockingContext
import com.anhhoang.aws.feature.s3reader.impl.local.S3ReaderLocalDataSource
import com.anhhoang.aws.feature.s3reader.impl.local.S3ReaderLocalDataSourceImpl
import com.anhhoang.aws.feature.s3reader.impl.local.datastore.UserSettingsSerializer
import com.anhhoang.aws.feature.s3reader.impl.local.datastore.UserSettings
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@InstallIn(SingletonComponent::class)
@Module
internal object LocalModule {

    // The datastore works with IO dispatchers by default, but by providing a coroutine context
    // it gives control and testability over the DataStore, in case hilt testing with real
    // instance is employed. Basically just a good practice that costs absolutely nothing.
    @Singleton
    @Provides
    fun provideDataStore(
        @ApplicationContext context: Context,
        @BlockingContext coroutineContext: CoroutineContext,
        serializer: UserSettingsSerializer,
    ): DataStore<UserSettings> = DataStoreFactory.create(
        serializer = serializer,
        scope = CoroutineScope(coroutineContext),
        produceFile = { context.dataStoreFile("user_settings") }
    )

}

@InstallIn(SingletonComponent::class)
@Module
internal interface LocalBindsModule {
    @Singleton
    @Binds
    fun bindLocalDataSource(impl: S3ReaderLocalDataSourceImpl): S3ReaderLocalDataSource
}
