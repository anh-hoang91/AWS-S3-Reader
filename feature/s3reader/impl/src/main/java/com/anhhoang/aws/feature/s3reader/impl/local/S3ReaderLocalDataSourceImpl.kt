package com.anhhoang.aws.feature.s3reader.impl.local

import androidx.paging.PagingSource
import com.anhhoang.aws.core.coroutines.executors.BlockingContext
import com.anhhoang.aws.feature.s3reader.api.local.FileDataDao
import com.anhhoang.aws.feature.s3reader.api.local.FileDataEntity
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/** Implementation of [S3ReaderLocalDataSource] with local database and datastore. */
internal class S3ReaderLocalDataSourceImpl @Inject constructor(
    @BlockingContext private val coroutineContext: CoroutineContext,
    private val fileDataDao: FileDataDao,
) : S3ReaderLocalDataSource {
    override suspend fun saveFiles(parent: String?, files: List<FileDataEntity>, hasMore: Boolean) =
        withContext(NonCancellable + coroutineContext) {
            fileDataDao.deleteAndInsertFiles(parent, files, hasMore)
        }

    override fun getFiles(parent: String?): PagingSource<Int, FileDataEntity> =
        fileDataDao.getFiles(parent)

    override suspend fun deleteFile(key: String?) = withContext(NonCancellable + coroutineContext) {
        fileDataDao.deleteFile(key)
    }

    override suspend fun deleteAllFiles() = withContext(NonCancellable + coroutineContext) {
        fileDataDao.deleteAll()
    }
}