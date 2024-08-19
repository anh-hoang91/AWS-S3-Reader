package com.anhhoang.aws.feature.s3reader.impl.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.testing.asSnapshot
import com.amazonaws.services.s3.model.ListObjectsV2Result
import com.amazonaws.services.s3.model.S3ObjectSummary
import com.anhhoang.aws.feature.s3reader.api.data.model.FileData
import com.anhhoang.aws.feature.s3reader.api.data.model.FileType
import com.anhhoang.aws.feature.s3reader.api.local.FileDataEntity
import com.anhhoang.aws.feature.s3reader.impl.local.S3ReaderLocalDataSource
import com.anhhoang.aws.feature.s3reader.impl.local.datastore.UserSettings
import com.anhhoang.aws.feature.s3reader.impl.network.S3ReaderNetworkDataSource
import com.google.common.base.Verify.verify
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Instant
import java.util.Date

/** Tests for [FileRepositoryImpl]. */
@RunWith(RobolectricTestRunner::class)
class FileRepositoryImplTest {
    private val testDispatcher = StandardTestDispatcher()
    private val localDataSource = mockk<S3ReaderLocalDataSource>(relaxed = true)
    private val networkDataSource = mockk<S3ReaderNetworkDataSource>(relaxed = true)
    private val networkDataSourceFactory =
        S3ReaderNetworkDataSource.Factory { _, _ -> networkDataSource }

    private val sut = FileRepositoryImpl(
        coroutineContext = testDispatcher,
        localDataSource = localDataSource,
        networkDataSourceFactory = networkDataSourceFactory
    )

    @Before
    fun setUp() {
        coEvery { localDataSource.getUserSettings() } returns UserSettings(
            "accessKey", "secretKey", "bucketName", "eu-central-1"
        )
    }

    @Test
    fun `getFiles(), expect files received`() = runTest(testDispatcher) {
        val fakePagingSource = object : PagingSource<Int, FileDataEntity>() {
            override fun getRefreshKey(state: PagingState<Int, FileDataEntity>): Int? = null

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FileDataEntity> =
                LoadResult.Page(listOf(parentEntity, fileEntity, fileEntity2), null, null)

        }
        every { localDataSource.getFiles(any()) } returns fakePagingSource

        val result = sut.getFiles().asSnapshot()

        assertThat(result).containsExactly(parentFile, file, file2)
    }

    @Test
    fun `loadFiles(), expect files downloaded and saved to DB`() = runTest(testDispatcher) {
        coEvery { networkDataSource.getFiles(any()) } returns networkData

        val hasMore = sut.loadFiles()

        assertThat(hasMore).isFalse()

        coVerify(exactly = 1) {
            localDataSource.saveFiles(listOf(parentEntity, fileEntity, fileEntity2))
        }

    }

    @Test
    fun saveUserSettings() = runTest(testDispatcher) {
        sut.saveUserSettings("test", "test", "test", "test")

        coVerify(exactly = 1) {
            localDataSource.saveUserSettings(UserSettings("test", "test", "test", "test"))
        }
    }

    @Test
    fun clearUserSettings() = runTest(testDispatcher) {
        sut.clearUserSettings()

        coVerify(exactly = 1) { localDataSource.clearUserSettings() }
    }

    private companion object {

        val parentEntity = FileDataEntity(
            key = "parent_key",
            parent = null,
            size = 0,
            lastModified = null,
            type = FileType.FOLDER
        )

        val fileEntity = FileDataEntity(
            key = "key",
            parent = null,
            size = 100,
            lastModified = Instant.parse("2024-01-01T00:00:00Z"),
            type = FileType.FILE
        )
        val fileEntity2 = FileDataEntity(
            key = "key2",
            parent = null,
            size = 100,
            lastModified = Instant.parse("2024-01-01T00:00:00Z"),
            type = FileType.FILE
        )

        val parentFile = FileData(
            key = "parent_key",
            parent = null,
            size = 0,
            lastModified = null,
            type = FileType.FOLDER
        )

        val file = FileData(
            key = "key",
            parent = null,
            size = 100,
            lastModified = Instant.parse("2024-01-01T00:00:00Z"),
            type = FileType.FILE
        )
        val file2 = FileData(
            key = "key2",
            parent = null,
            size = 100,
            lastModified = Instant.parse("2024-01-01T00:00:00Z"),
            type = FileType.FILE
        )

        val networkData = ListObjectsV2Result().apply {
            commonPrefixes = listOf("parent_key")
            keyCount = 3
            (objectSummaries as ArrayList<S3ObjectSummary>).addAll(
                listOf(
                    S3ObjectSummary().apply {
                        key = "key"
                        size = 100
                        lastModified = Date.from(Instant.parse("2024-01-01T00:00:00Z"))
                    },
                    S3ObjectSummary().apply {
                        key = "key2"
                        size = 100
                        lastModified = Date.from(Instant.parse("2024-01-01T00:00:00Z"))
                    })
            )
        }
    }
}