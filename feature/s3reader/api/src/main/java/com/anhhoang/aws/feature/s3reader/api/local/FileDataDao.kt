package com.anhhoang.aws.feature.s3reader.api.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/** Data access object for interacting with the file data in the local database. */
@Dao
interface FileDataDao {
    @Query("SELECT * FROM file_data WHERE parent is null")
    fun getBase(): List<FileDataEntity>

    @Query("SELECT * FROM file_data WHERE (parent is NULL and :parent is NULL) OR parent = :parent")
    fun getFiles(parent: String? = null): PagingSource<Int, FileDataEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiles(files: List<FileDataEntity>)

    @Query("DELETE FROM file_data WHERE `key` = :key")
    suspend fun deleteFile(key: String? = null)

    @Query("DELETE FROM file_data")
    suspend fun deleteAll()
}