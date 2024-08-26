package com.anhhoang.aws.feature.s3reader.impl.local

import androidx.paging.PagingSource
import com.anhhoang.aws.feature.s3reader.api.local.FileDataEntity

/** Local data source for the s3 reader app. */
internal interface S3ReaderLocalDataSource {
    /** Save files to the local storage. */
    suspend fun saveFiles(parent: String?, files: List<FileDataEntity>, hasMore: Boolean)

    /** Get files from the local storage. */
    fun getFiles(parent: String? = null) : PagingSource<Int, FileDataEntity>
    /** Get user settings from the local storage. */
    suspend fun deleteFile(key: String? = null)

    /** Delete all files from the local storage. */
    suspend fun deleteAllFiles()
}