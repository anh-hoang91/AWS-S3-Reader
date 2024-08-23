package com.anhhoang.aws.feature.usersettings.impl.data

import com.anhhoang.aws.core.coroutines.executors.BlockingContext
import com.anhhoang.aws.feature.usersettings.api.data.UserSettingsRepository
import com.anhhoang.aws.feature.usersettings.impl.local.UserSettingsLocalDataSource
import com.anhhoang.aws.feature.usersettings.api.data.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/** Implementation of [UserSettingsRepository]. */
class UserSettingsRepositoryImpl @Inject internal constructor(
    @BlockingContext private val coroutineContext: CoroutineContext,
    private val localDataSource: UserSettingsLocalDataSource,
) : UserSettingsRepository {

    override suspend fun getUserSettings(): UserSettings =
        withContext(coroutineContext) { localDataSource.getUserSettings() }

    override fun getUserSettingsFlow(): Flow<UserSettings> = localDataSource.getUserSettingsFlow()

    override suspend fun hasAccess(): Boolean = hasAccess(localDataSource.getUserSettings())

    override fun hasAccessFlow(): Flow<Boolean> =
        localDataSource.getUserSettingsFlow().map { hasAccess(it) }

    private fun hasAccess(userSettings: UserSettings) =
        userSettings.accessKey.isNotEmpty() &&
                userSettings.secretKey.isNotEmpty() &&
                userSettings.bucketName.isNotEmpty()

    override suspend fun saveUserSettings(
        accessKey: String,
        secretKey: String,
        bucketName: String,
        region: String,
    ) = withContext(coroutineContext) {
        localDataSource.saveUserSettings(
            UserSettings(
                accessKey,
                secretKey,
                bucketName,
                region
            )
        )
    }

    override suspend fun clearUserSettings() = localDataSource.clearUserSettings()
}