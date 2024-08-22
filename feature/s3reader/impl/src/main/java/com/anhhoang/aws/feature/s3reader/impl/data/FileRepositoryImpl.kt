package com.anhhoang.aws.feature.s3reader.impl.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.services.s3.AmazonS3Client
import com.anhhoang.aws.core.coroutines.executors.LightweightContext
import com.anhhoang.aws.feature.s3reader.api.data.FileRepository
import com.anhhoang.aws.feature.s3reader.api.data.model.FileData
import com.anhhoang.aws.feature.s3reader.api.data.model.FileType
import com.anhhoang.aws.feature.s3reader.api.local.FileDataEntity
import com.anhhoang.aws.feature.s3reader.impl.local.S3ReaderLocalDataSource
import com.anhhoang.aws.feature.s3reader.impl.local.datastore.UserSettings
import com.anhhoang.aws.feature.s3reader.impl.network.S3ReaderNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/** Implementation of [FileRepository]. */
class FileRepositoryImpl @Inject internal constructor(
    @LightweightContext private val coroutineContext: CoroutineContext,
    private val localDataSource: S3ReaderLocalDataSource,
    private val networkDataSourceFactory: S3ReaderNetworkDataSource.Factory,
) : FileRepository {

    private lateinit var networkDataSource: S3ReaderNetworkDataSource

    private suspend fun getNetworkDataSource(): S3ReaderNetworkDataSource =
        if (!this::networkDataSource.isInitialized) {
            withContext(coroutineContext) {
                val userSettings = localDataSource.getUserSettings()
                val credentials =
                    BasicAWSCredentials(userSettings.accessKey, userSettings.secretKey)
                val s3Client = AmazonS3Client(credentials, Region.getRegion(userSettings.region))
                networkDataSourceFactory.create(s3Client, userSettings.bucketName)
            }
        } else {
            networkDataSource
        }

    override fun getFiles(parent: String?): Flow<PagingData<FileData>> = Pager(
        config = PagingConfig(pageSize = 30),
        pagingSourceFactory = {
            localDataSource.getFiles(parent)
        },
    ).flow.map { it.map { f -> f.toModel() } }


    override suspend fun loadFiles(parent: String?, startAfter: String?): Boolean {
        val result = getNetworkDataSource().getFiles(parent)

        val fileEntities = buildList {
            val folders = result.commonPrefixes.mapNotNull {
                FileDataEntity(
                    key = it,
                    parent = parent,
                    size = 0,
                    lastModified = null,
                    type = FileType.FOLDER,
                )
            }
            val files = result.objectSummaries.mapNotNull {
                FileDataEntity(
                    key = it.key,
                    parent = parent,
                    size = it.size,
                    lastModified = it.lastModified.toInstant(),
                    FileType.FILE,
                )
            }

            addAll(folders)
            addAll(files)
        }
        val hasMore = result.nextContinuationToken != null
        localDataSource.saveFiles(parent, fileEntities, hasMore)

        return hasMore
    }

    override suspend fun hasAccess(): Boolean = hasAccess(localDataSource.getUserSettings())

    override fun hasAccessFlow(): Flow<Boolean> =
        localDataSource.getUserSettingsFlow().map { hasAccess(it) }

    private fun hasAccess(userSettings: UserSettings) =
        userSettings.accessKey.isNotEmpty() &&
                userSettings.secretKey.isNotEmpty() &&
                userSettings.bucketName.isNotEmpty()

    override suspend fun saveUserSettings(
        accessKey: String,
        secretKey: String,
        bucketName: String,
        region: String,
    ) {
        val regionName = region.ifEmpty { "eu-central-1" }

        localDataSource.saveUserSettings(UserSettings(accessKey, secretKey, bucketName, regionName))
    }

    override suspend fun clearUserSettings() = localDataSource.clearUserSettings()
}