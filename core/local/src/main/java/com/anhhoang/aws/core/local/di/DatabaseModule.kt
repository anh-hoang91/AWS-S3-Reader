package com.anhhoang.aws.core.local.di

import android.content.Context
import androidx.room.Room
import com.anhhoang.aws.core.local.database.AwsStorageDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AwsStorageDatabase = Room.databaseBuilder(
        context = context,
        klass = AwsStorageDatabase::class.java,
        name = "s3_reader.db",
    ).build()
}