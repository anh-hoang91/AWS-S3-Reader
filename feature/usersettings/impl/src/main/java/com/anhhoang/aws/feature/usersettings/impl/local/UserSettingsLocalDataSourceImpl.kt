package com.anhhoang.aws.feature.usersettings.impl.local

import androidx.datastore.core.DataStore
import com.anhhoang.aws.core.coroutines.executors.BlockingContext
import com.anhhoang.aws.feature.usersettings.api.data.UserSettings
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/** Implementation of [UserSettingsLocalDataSource] with datastore. */
internal class UserSettingsLocalDataSourceImpl @Inject constructor(
    @BlockingContext private val coroutineContext: CoroutineContext,
    private val userSettingsDataStore: DataStore<UserSettings>
) : UserSettingsLocalDataSource {

    override suspend fun getUserSettings(): UserSettings =
        withContext(coroutineContext) {
            userSettingsDataStore.data.first()
        }

    override fun getUserSettingsFlow(): Flow<UserSettings> =
        userSettingsDataStore.data

    override suspend fun saveUserSettings(userSettings: UserSettings) {
        userSettingsDataStore.updateData { userSettings }
    }

    override suspend fun clearUserSettings() = saveUserSettings(UserSettings())
}