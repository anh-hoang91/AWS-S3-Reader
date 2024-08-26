package com.anhhoang.aws.feature.s3reader.api.data.model

import java.time.Instant

/** Domain model for a file object. */
data class FileData(
    val key: String,
    val parent: String?,
    val size: Long,
    val lastModified: Instant?,
    val type: FileType,
)
