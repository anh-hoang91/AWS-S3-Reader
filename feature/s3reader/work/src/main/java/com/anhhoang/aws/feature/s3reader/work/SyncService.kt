package com.anhhoang.aws.feature.s3reader.work

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.time.Duration
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/** Sync service to keep implementation complexity away from usages. */
@Singleton
class SyncService @Inject internal constructor(
    @ApplicationContext private val context: Context,
) {
    fun getExistingPeriodicWorkResourceState(): Flow<WorkInfo?> =
        WorkManager.getInstance(context).getWorkInfoByIdFlow(
            PERIODIC_ID
        )

    fun getExistingOneTimeWorkResourceState(): Flow<WorkInfo?> =
        WorkManager.getInstance(context).getWorkInfoByIdFlow(ONE_TIME_ID)

    fun startPeriodicWork() {
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            buildPeriodicWorkRequest()
        )
    }

    private fun buildPeriodicWorkRequest(): PeriodicWorkRequest =
        PeriodicWorkRequestBuilder<SyncWorker>(Duration.ofMinutes(15)) // 15 is minimum
            .setId(PERIODIC_ID)
            .setBackoffCriteria(
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                Duration.ofSeconds(30)
            )
            .build()

    fun startOneTimeWork() {
        WorkManager.getInstance(context).enqueueUniqueWork(
            ONE_TIME_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            buildOneTimeWorkRequest(),
        )
    }

    private fun buildOneTimeWorkRequest(): OneTimeWorkRequest =
        OneTimeWorkRequestBuilder<SyncWorker>().setId(ONE_TIME_ID).build()

    private companion object {
        private const val PERIODIC_WORK_NAME = "PeriodicSyncWorker"
        private const val ONE_TIME_WORK_NAME = "OneTimeSyncWorker"

        // Static id for the periodic work request to have consistent worker experience. In case of
        // on-going work and the app is killed and then restarted before the worker finishes, then
        // This will allow us to have access to the very same worker task.
        private val PERIODIC_ID = UUID.fromString("acb6e37c-9699-4604-b6e9-84adc3583959")

        // Random is fine for one time requests, the ID is generated once an app start anyway.
        private val ONE_TIME_ID = UUID.randomUUID()
    }
}