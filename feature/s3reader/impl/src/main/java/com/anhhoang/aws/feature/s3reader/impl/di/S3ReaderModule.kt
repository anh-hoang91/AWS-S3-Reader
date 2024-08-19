package com.anhhoang.aws.feature.s3reader.impl.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.amazonaws.services.s3.AmazonS3Client
import com.anhhoang.aws.core.coroutines.executors.BlockingContext
import com.anhhoang.aws.feature.s3reader.impl.local.S3ReaderLocalDataSource
import com.anhhoang.aws.feature.s3reader.impl.local.S3ReaderLocalDataSourceImpl
import com.anhhoang.aws.feature.s3reader.impl.local.S3ReaderLocalDataSourceImpl_Factory
import com.anhhoang.aws.feature.s3reader.impl.local.datastore.UserSettingsSerializer
import com.anhhoang.aws.feature.s3reader.impl.local.datastore.UserSettings
import com.anhhoang.aws.feature.s3reader.impl.network.S3ReaderNetworkDataSource
import com.anhhoang.aws.feature.s3reader.impl.network.S3ReaderNetworkDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@InstallIn(SingletonComponent::class)
@Module
internal object S3ReaderModule {

    // The datastore works with IO dispatchers by default, but by providing a coroutine context
    // it gives control and testability over the DataStore, in case hilt testing with real
    // instance is employed. Basically just a good practice that costs absolutely nothing.
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
internal interface S3ReaderBindsModule {
    @Binds
    fun bindLocalDataSource(impl: S3ReaderLocalDataSourceImpl): S3ReaderLocalDataSource

    @Binds
    fun bindNetworkDataSource(impl: S3ReaderNetworkDataSourceImpl.Factory): S3ReaderNetworkDataSource.Factory
}
