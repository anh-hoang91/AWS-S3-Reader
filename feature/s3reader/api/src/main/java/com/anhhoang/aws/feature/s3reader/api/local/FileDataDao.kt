package com.anhhoang.aws.feature.s3reader.api.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

/** Data access object for interacting with the file data in the local database. */
@Dao
interface FileDataDao {
    @Query("SELECT * FROM file_data WHERE parent is null")
    fun getBase(): List<FileDataEntity>

    @Query("SELECT * FROM file_data WHERE (parent is NULL and :parent is NULL) OR parent = :parent")
    fun getFiles(parent: String? = null): PagingSource<Int, FileDataEntity>

    @Query("SELECT * FROM file_data WHERE (parent is NULL and :parent is NULL) OR parent = :parent")
    suspend fun getFilesList(parent: String? = null): List<FileDataEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiles(files: List<FileDataEntity>)

    @Query("DELETE FROM file_data WHERE `key` = :key")
    suspend fun deleteFile(key: String? = null)

    @Delete
    suspend fun deleteFiles(files: List<FileDataEntity>)

    @Query("DELETE FROM file_data")
    suspend fun deleteAll()

    @Transaction
    suspend fun deleteAndInsertFiles(
        parent: String?,
        files: List<FileDataEntity>,
        hasMore: Boolean
    ) {
        // Big assumptions. In case of pagination, a lot of files will be needlessly deleted.
        // This should be handled gracefully with better data design to keep track of synced.
        if (!hasMore) {
            val oldFiles = getFilesList(parent)
            val toDelete = oldFiles.filterNot { it in files }

            deleteFiles(toDelete)
        }

        insertFiles(files)
    }

}