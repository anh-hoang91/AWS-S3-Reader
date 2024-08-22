package com.anhhoang.aws.feature.s3reader.api.data

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.anhhoang.aws.feature.s3reader.api.data.model.FileData
import kotlinx.coroutines.flow.Flow

/** Repository for interacting with the s3 files. */
interface FileRepository {
    /** Get paginated files. */
    fun getFiles(parent: String? = null): Flow<PagingData<FileData>>

    /**
     * Load more files from the server.
     *
     * @param parent The parent directory path to load files for.
     * @param startAfter The key to start loading files from in case there are more files to load.
     *
     * @return True if there are more files to load for the path, false otherwise.
     */
    suspend fun loadFiles(parent: String? = null, startAfter: String? = null): Boolean

    /** Check if the user have settings to access the bucket. */
    suspend fun hasAccess(): Boolean

    /** Get a flow of the user's access settings. */
    fun hasAccessFlow(): Flow<Boolean>

    /** Save the user's access settings. */
    suspend fun saveUserSettings(accessKey: String, secretKey: String, bucketName: String, region: String = "")

    /** Clear up user's settings. */
    suspend fun clearUserSettings()
}