package com.anhhoang.aws.feature.s3reader.di

import com.anhhoang.aws.core.nav.NavSubGraph
import com.anhhoang.aws.feature.s3reader.api.data.FileRepository
import com.anhhoang.aws.feature.s3reader.impl.data.FileRepositoryImpl
import com.anhhoang.aws.feature.s3reader.ui.impl.S3ReaderNavigation
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@InstallIn(SingletonComponent::class)
@Module
internal interface S3ReaderModule {
    @Binds
    fun bindLocalDataSource(impl: FileRepositoryImpl): FileRepository

    @Binds
    @IntoSet
    fun bindNavGraph(impl: S3ReaderNavigation): NavSubGraph
}