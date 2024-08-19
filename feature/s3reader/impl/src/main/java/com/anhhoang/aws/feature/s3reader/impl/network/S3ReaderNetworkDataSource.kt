package com.anhhoang.aws.feature.s3reader.impl.network

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ListObjectsV2Result

internal interface S3ReaderNetworkDataSource {
    suspend fun getFiles(parentKey: String? = null): ListObjectsV2Result


    fun interface Factory {
        fun create(s3Client: AmazonS3Client, bucketName: String): S3ReaderNetworkDataSource
    }
}