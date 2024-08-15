package com.anhhoang.aws.feature.s3reader.api.local

import androidx.room.Entity
import com.anhhoang.aws.feature.s3reader.api.data.model.FileType
import java.time.Instant

/** Database entity for storing file data. */
@Entity(tableName = "file_data", primaryKeys = ["key", "parent"])
data class FileDataEntity(
    val key: String,
    val parent: String?,
    val size: Long,
    val lastModified: Instant,
    val type: FileType,
)