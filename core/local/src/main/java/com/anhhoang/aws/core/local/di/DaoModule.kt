package com.anhhoang.aws.core.local.di

import com.anhhoang.aws.core.local.database.AwsStorageDatabase
import com.anhhoang.aws.feature.s3reader.api.local.FileDataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
internal object DaoModule {
    @Provides
    fun provideFileDataDao(database: AwsStorageDatabase): FileDataDao = database.fileDataDao()
}