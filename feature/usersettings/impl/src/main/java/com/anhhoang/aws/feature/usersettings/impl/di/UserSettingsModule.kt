package com.anhhoang.aws.feature.usersettings.impl.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.anhhoang.aws.core.coroutines.executors.BlockingContext
import com.anhhoang.aws.feature.usersettings.api.data.UserSettings
import com.anhhoang.aws.feature.usersettings.impl.local.UserSettingsLocalDataSource
import com.anhhoang.aws.feature.usersettings.impl.local.UserSettingsLocalDataSourceImpl
import com.anhhoang.aws.feature.usersettings.impl.local.datastore.UserSettingsSerializer
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
internal object UserSettingsModule {

    // The datastore works with IO dispatchers by default, but by providing a coroutine context
    // it gives control and testability over the DataStore, in case hilt testing with real
    // instance is employed. Basically just a good practice that costs absolutely nothing.
    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context,
        @BlockingContext coroutineContext: CoroutineContext,
        serializer: UserSettingsSerializer,
    ): DataStore<UserSettings> = DataStoreFactory.create(
        serializer = serializer,
        scope = CoroutineScope(coroutineContext),
        produceFile = { context.dataStoreFile("user_settings.txt") }
    )
}

@InstallIn(SingletonComponent::class)
@Module
internal interface UserSettingsBindsModule {
    @Binds
    fun bindUserSettingsLocalDataSource(impl: UserSettingsLocalDataSourceImpl): UserSettingsLocalDataSource
}
