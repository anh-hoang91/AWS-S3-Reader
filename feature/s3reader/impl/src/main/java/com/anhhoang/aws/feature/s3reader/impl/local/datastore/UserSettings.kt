package com.anhhoang.aws.feature.s3reader.impl.local.datastore

import kotlinx.serialization.Serializable

/** User settings to be saved in the data store. */
@Serializable
internal data class UserSettings(
    val accessKey: String = "",
    val secretKey: String = "",
    val bucketName: String = "",
    val region: String = "eu-central-1",
)
