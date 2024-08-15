package com.anhhoang.aws.feature.s3reader.api.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/** Data access object for interacting with the file data in the local database. */
@Dao
interface FileDataDao {
    @Query("SELECT * FROM file_data WHERE parent = :parent")
    suspend fun getFiles(parent: String? = null): PagingSource<Int, FileDataEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiles(files: List<FileDataEntity>)

    @Query("DELETE FROM file_data WHERE parent = :parent")
    suspend fun deleteFiles(parent: String? = null)
}