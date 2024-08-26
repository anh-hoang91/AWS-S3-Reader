package com.anhhoang.aws.feature.s3reader.impl.network

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ListObjectsV2Request
import com.amazonaws.services.s3.model.ListObjectsV2Result
import com.anhhoang.aws.core.coroutines.executors.BlockingContext
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

internal class S3ReaderNetworkDataSourceImpl @AssistedInject constructor(
    @BlockingContext private val coroutineContext: CoroutineContext,
    @Assisted private val s3Client: AmazonS3Client,
    @Assisted private val bucketName: String,
) : S3ReaderNetworkDataSource {

    @AssistedFactory
    interface Factory : S3ReaderNetworkDataSource.Factory {
        override fun create(
            s3Client: AmazonS3Client,
            bucketName: String
        ): S3ReaderNetworkDataSourceImpl
    }

    override suspend fun getFiles(parentKey: String?): ListObjectsV2Result =
        withContext(coroutineContext) {
            s3Client.listObjectsV2(ListObjectsV2Request().apply {
                bucketName = this@S3ReaderNetworkDataSourceImpl.bucketName
                delimiter = "/"
                prefix = parentKey
            })
        }
}