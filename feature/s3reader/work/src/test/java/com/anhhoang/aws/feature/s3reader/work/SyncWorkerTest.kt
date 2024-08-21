package com.anhhoang.aws.feature.s3reader.work

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import com.anhhoang.aws.feature.s3reader.api.data.FileRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Tests for [SyncWorker]. */
@RunWith(RobolectricTestRunner::class)
class SyncWorkerTest {
    private val testDispatcher = StandardTestDispatcher()
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val repository = mockk<FileRepository>(relaxed = true)

    private val sut = object : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker = SyncWorker(appContext, workerParameters, repository)
    }

    @Test
    fun `doWork() returns success`() = runTest(testDispatcher) {
        coEvery { repository.hasAccess() } returns true

        val worker = TestListenableWorkerBuilder<SyncWorker>(context)
            .setWorkerFactory(sut)
            .build()

        val result = worker.doWork()

        assertThat(result).isEqualTo(ListenableWorker.Result.success())
    }

    @Test
    fun `doWork(), on failure, expect failure returned`() = runTest(testDispatcher) {
        coEvery { repository.hasAccess() } returns true

        coEvery { repository.loadFiles(any(), any()) } throws Exception()

        val worker = TestListenableWorkerBuilder<SyncWorker>(context)
            .setWorkerFactory(sut)
            .build()

        val result = worker.doWork()

        assertThat(result).isEqualTo(ListenableWorker.Result.failure())
    }

    @Test
    fun `doWork(), no access, expect failure returned`() = runTest(testDispatcher) {
        coEvery { repository.hasAccess() } returns false

        val worker = TestListenableWorkerBuilder<SyncWorker>(context)
            .setWorkerFactory(sut)
            .build()

        val result = worker.doWork()

        assertThat(result).isEqualTo(ListenableWorker.Result.failure())
    }
}