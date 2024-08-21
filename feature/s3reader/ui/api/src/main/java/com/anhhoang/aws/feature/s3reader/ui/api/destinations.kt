package com.anhhoang.aws.feature.s3reader.ui.api

import kotlinx.serialization.Serializable

/** Destination for file explorer screen. */
@Serializable
data class S3FileExplorerDestination(val parent: String? = null)

/** Destination for user settings screen. */
@Serializable
object SettingsDestination

/** Destination for S3 reader feature sub-graph. */
@Serializable
object S3ReaderGraph