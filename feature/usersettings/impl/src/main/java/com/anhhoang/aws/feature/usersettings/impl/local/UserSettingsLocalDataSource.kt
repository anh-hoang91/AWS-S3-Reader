package com.anhhoang.aws.feature.usersettings.impl.local

import com.anhhoang.aws.feature.usersettings.api.data.UserSettings
import kotlinx.coroutines.flow.Flow

/** User settings local data source for the app. */
internal interface UserSettingsLocalDataSource {

    /** Get user settings from the local storage. */
    suspend fun getUserSettings(): UserSettings

    /** Get user settings from the local storage as flow. */
    fun getUserSettingsFlow(): Flow<UserSettings>

    /** Get user settings from the local storage. */
    suspend fun saveUserSettings(userSettings: UserSettings)

    /** Get user settings from the local storage. */
    suspend fun clearUserSettings()
}