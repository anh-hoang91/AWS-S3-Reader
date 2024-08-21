package com.anhhoang.aws.feature.s3reader.work

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.anhhoang.aws.feature.s3reader.api.data.FileRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CancellationException

/** Worker for syncing files from the server periodically or on demand. */
@VisibleForTesting(otherwise = VisibleForTesting.PACKAGE_PRIVATE)
@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val fileRepository: FileRepository
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        if (fileRepository.hasAccess().not()) {
            return Result.failure()
        }

        try {
            val parentKey = inputData.getString(PARENT_KEY)
            fileRepository.loadFiles(parentKey)

            return Result.success()
        } catch (e: Exception) {
            // Rethrow cancellation exceptions for coroutines to handle
            if (e is CancellationException) throw e

            return Result.failure()
        }
    }

    companion object {
        const val PARENT_KEY = "PARENT_KEY"
    }
}