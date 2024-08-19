package com.anhhoang.aws.feature.s3reader.impl.local

import androidx.datastore.core.DataStore
import androidx.paging.PagingSource
import com.anhhoang.aws.core.coroutines.executors.BlockingContext
import com.anhhoang.aws.feature.s3reader.api.local.FileDataDao
import com.anhhoang.aws.feature.s3reader.api.local.FileDataEntity
import com.anhhoang.aws.feature.s3reader.impl.local.datastore.UserSettings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/** Implementation of [S3ReaderLocalDataSource] with local database and datastore. */
internal class S3ReaderLocalDataSourceImpl @Inject constructor(
    @BlockingContext private val coroutineContext: CoroutineContext,
    private val fileDataDao: FileDataDao,
    private val userSettingsDataStore: DataStore<UserSettings>
) : S3ReaderLocalDataSource {
    override suspend fun saveFiles(files: List<FileDataEntity>) = withContext(coroutineContext) {
        fileDataDao.insertFiles(files)
    }

    override fun getFiles(parent: String?): PagingSource<Int, FileDataEntity> =
        fileDataDao.getFiles(parent)

    override suspend fun deleteFile(key: String?) = withContext(coroutineContext) {
        fileDataDao.deleteFile(key)
    }

    override suspend fun deleteAllFiles() = withContext(coroutineContext) {
        fileDataDao.deleteAll()
    }

    override suspend fun getUserSettings(): UserSettings = withContext(coroutineContext) {
        userSettingsDataStore.data.first()
    }

    override suspend fun saveUserSettings(userSettings: UserSettings) {
        userSettingsDataStore.updateData { userSettings }
    }

    override suspend fun clearUserSettings() {
        userSettingsDataStore.updateData { UserSettings() }
    }
}