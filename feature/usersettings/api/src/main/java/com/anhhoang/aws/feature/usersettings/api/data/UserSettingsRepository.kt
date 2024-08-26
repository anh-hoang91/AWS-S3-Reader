package com.anhhoang.aws.feature.usersettings.api.data

import kotlinx.coroutines.flow.Flow

/** Repository for interacting with the user settings. */
interface UserSettingsRepository {

    /** Get the user's access settings. */
    suspend fun getUserSettings(): UserSettings

    /** Get the user's access settings flow. */
    fun getUserSettingsFlow(): Flow<UserSettings>

    /** Check if the user have settings to access the bucket. */
    suspend fun hasAccess(): Boolean

    /** Get a flow of the user's access settings. */
    fun hasAccessFlow(): Flow<Boolean>

    /** Save the user's access settings. */
    suspend fun saveUserSettings(
        accessKey: String,
        secretKey: String,
        bucketName: String,
        region: String = "",
    )

    /** Clear up user's settings. */
    suspend fun clearUserSettings()
}