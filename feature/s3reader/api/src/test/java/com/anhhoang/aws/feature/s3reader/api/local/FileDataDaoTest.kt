package com.anhhoang.aws.feature.s3reader.api.local

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.testing.asSnapshot
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.anhhoang.aws.core.local.database.AwsStorageDatabase
import com.anhhoang.aws.feature.s3reader.api.data.model.FileType
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Instant

/** Tests for [FileDataDao]. */
@RunWith(RobolectricTestRunner::class)
class FileDataDaoTest {
    private val testDispatcher = StandardTestDispatcher()
    private val database = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        AwsStorageDatabase::class.java,
    )
        .allowMainThreadQueries()
        .build()
    private val dao = database.fileDataDao()

    @Test
    fun `get files, no items added, expect empty`() = runTest(testDispatcher) {
        val result = Pager(
            config = PagingConfig(5, 0),
            pagingSourceFactory = { dao.getFiles("") }
        ).flow.asSnapshot()

        assertThat(result).isEmpty()
    }

    @Test
    fun `insert files, expect files are added`() = runTest(testDispatcher) {
        dao.insertFiles(listOf(parentEntity))
        dao.insertFiles(listOf(fileEntity, fileEntity2))

        val parentResult = Pager(
            config = PagingConfig(5, 0),
            pagingSourceFactory = { dao.getFiles() }
        ).flow.asSnapshot()

        val childResult = Pager(
            config = PagingConfig(5, 0),
            pagingSourceFactory = { dao.getFiles("parent_key") }
        ).flow.asSnapshot()

        assertThat(parentResult).containsExactly(parentEntity)
        assertThat(childResult).containsExactly(fileEntity, fileEntity2)
    }

    @Test
    fun `delete files, expect files are deleted`() = runTest(testDispatcher) {
        dao.insertFiles(listOf(parentEntity))
        dao.insertFiles(listOf(fileEntity, fileEntity2))

        dao.deleteFiles("parent_key")

        val parentResult = Pager(
            config = PagingConfig(5, 0),
            pagingSourceFactory = { dao.getFiles() }
        ).flow.asSnapshot()

        val childResult = Pager(
            config = PagingConfig(5, 0),
            pagingSourceFactory = { dao.getFiles("parent_key") }
        ).flow.asSnapshot()

        assertThat(parentResult).isEmpty()
        assertThat(childResult).isEmpty()
    }

    private companion object {
        val parentEntity = FileDataEntity(
            key = "parent_key",
            parent = null,
            size = 100,
            lastModified = Instant.parse("2024-01-01T00:00:00Z"),
            type = FileType.FOLDER
        )

        val fileEntity = FileDataEntity(
            key = "key",
            parent = "parent_key",
            size = 100,
            lastModified = Instant.parse("2024-01-01T00:00:00Z"),
            type = FileType.FILE
        )
        val fileEntity2 = FileDataEntity(
            key = "key2",
            parent = "parent_key",
            size = 100,
            lastModified = Instant.parse("2024-01-01T00:00:00Z"),
            type = FileType.FILE
        )
    }
}