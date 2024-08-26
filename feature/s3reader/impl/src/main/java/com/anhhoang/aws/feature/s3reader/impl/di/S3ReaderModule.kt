package com.anhhoang.aws.feature.s3reader.impl.di

import com.anhhoang.aws.feature.s3reader.impl.local.S3ReaderLocalDataSource
import com.anhhoang.aws.feature.s3reader.impl.local.S3ReaderLocalDataSourceImpl
import com.anhhoang.aws.feature.s3reader.impl.network.S3ReaderNetworkDataSource
import com.anhhoang.aws.feature.s3reader.impl.network.S3ReaderNetworkDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
internal interface S3ReaderModule {
    @Binds
    fun bindLocalDataSource(impl: S3ReaderLocalDataSourceImpl): S3ReaderLocalDataSource

    @Binds
    fun bindNetworkDataSource(impl: S3ReaderNetworkDataSourceImpl.Factory): S3ReaderNetworkDataSource.Factory
}
