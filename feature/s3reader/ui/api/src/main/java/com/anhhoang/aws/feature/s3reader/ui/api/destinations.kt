package com.anhhoang.aws.feature.s3reader.ui.api

import kotlinx.serialization.Serializable

// Composable destinations should be accessible within the graph only. Between feature navigation
// should navigate to the sub-graph in combination with deep link if more specific screen is needed.
/** Destination for file explorer screen. */
@Serializable
data class S3FileExplorerDestination(val parent: String? = null)

/** Destination for S3 reader feature sub-graph. */
@Serializable
object S3ReaderGraphDestination