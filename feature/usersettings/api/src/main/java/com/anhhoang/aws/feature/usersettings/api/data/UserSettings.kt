package com.anhhoang.aws.feature.usersettings.api.data

import kotlinx.serialization.Serializable

/** User settings to be saved in the data store. */
@Serializable
data class UserSettings(
    val accessKey: String = "",
    val secretKey: String = "",
    val bucketName: String = "",
    val region: String = "",
)
