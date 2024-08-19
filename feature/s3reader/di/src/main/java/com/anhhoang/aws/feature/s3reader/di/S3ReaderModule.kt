package com.anhhoang.aws.feature.s3reader.di

import com.anhhoang.aws.feature.s3reader.api.data.FileRepository
import com.anhhoang.aws.feature.s3reader.impl.data.FileRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
internal interface S3ReaderModule {
    @Binds
    fun bindLocalDataSource(impl: FileRepositoryImpl): FileRepository
}