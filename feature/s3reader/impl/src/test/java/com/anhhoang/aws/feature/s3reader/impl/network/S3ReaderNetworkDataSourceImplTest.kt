package com.anhhoang.aws.feature.s3reader.impl.network

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.ListObjectsV2Request
import com.amazonaws.services.s3.model.ListObjectsV2Result
import com.amazonaws.services.s3.model.S3ObjectSummary
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertFailsWith

/** Tests for [S3ReaderNetworkDataSourceImpl]. */
@RunWith(RobolectricTestRunner::class)
class S3ReaderNetworkDataSourceImplTest {
    private val testDispatcher = StandardTestDispatcher()
    private val s3Client = mockk<AmazonS3Client>()
    private val bucketName = "test-bucket"

    private val sut = S3ReaderNetworkDataSourceImpl(
        coroutineContext = testDispatcher,
        s3Client = s3Client,
        bucketName = bucketName
    )

    @Test
    fun `getFiles(), expect files are propagated up`() = runTest(testDispatcher) {
        val expectedResult = ListObjectsV2Result().apply {
            commonPrefixes = listOf("f1", "f2")
            keyCount = 4
            (objectSummaries as ArrayList<S3ObjectSummary>).addAll(
                listOf(S3ObjectSummary().apply {
                    key = "fi1"
                    size = 8
                },
                    S3ObjectSummary().apply {
                        key = "fi2"
                        size = 12
                    })
            )
        }

        every { s3Client.listObjectsV2(any<ListObjectsV2Request>()) } returns expectedResult

        val result = sut.getFiles()

        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `getFiles(), error is propagated up`() = runTest(testDispatcher) {
        every { s3Client.listObjectsV2(any<ListObjectsV2Request>()) } throws Exception("test")

        assertFailsWith<Exception>(message = "test") {
            sut.getFiles()
        }
    }
}