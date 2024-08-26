package com.anhhoang.aws.feature.s3reader.impl.local

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.testing.asSnapshot
import com.anhhoang.aws.feature.s3reader.api.data.model.FileType
import com.anhhoang.aws.feature.s3reader.api.local.FileDataDao
import com.anhhoang.aws.feature.s3reader.api.local.FileDataEntity
import com.google.common.truth.Truth.assertThat
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Instant

/** Tests for [S3ReaderLocalDataSourceImpl]. */
@RunWith(RobolectricTestRunner::class)
class S3ReaderLocalDataSourceImplTest {
    private val testDispatcher = StandardTestDispatcher()
    private val dao = mockk<FileDataDao>(relaxed = true)

    private val sut = S3ReaderLocalDataSourceImpl(
        coroutineContext = testDispatcher,
        fileDataDao = dao,
    )

    @Test
    fun getFiles() = runTest(testDispatcher) {
        every { dao.getFiles(any()) } returns object : PagingSource<Int, FileDataEntity>() {
            override fun getRefreshKey(state: PagingState<Int, FileDataEntity>): Int? = null

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FileDataEntity> =
                LoadResult.Page(listOf(fileEntity, fileEntity2), null, null)
        }

        val result = sut.getFiles()
        val pagerResult = Pager(PagingConfig(pageSize = 20)) { result }.flow.asSnapshot()

        assertThat(pagerResult).containsExactly(fileEntity, fileEntity2)
    }

    @Test
    fun `saveFiles(), expect insert files called on DAO`() = runTest(testDispatcher) {
        sut.saveFiles(null, listOf(parentEntity, fileEntity), false)

        coVerify(exactly = 1) { dao.insertFiles(listOf(parentEntity, fileEntity)) }
    }

    @Test
    fun `deleteFile(), expect delete file called on DAO`() = runTest(testDispatcher) {
        sut.deleteFile("key")

        coVerify(exactly = 1) { dao.deleteFile("key") }
    }

    @Test
    fun deleteAllFiles() = runTest(testDispatcher) {
        sut.deleteAllFiles()

        coVerify(exactly = 1) { dao.deleteAll() }
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