package com.anhhoang.aws.feature.s3reader.api.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import com.anhhoang.aws.feature.s3reader.api.data.model.FileType
import java.time.Instant

/** Database entity for storing file data. */
@Entity(
    tableName = "file_data",
    foreignKeys = [
        ForeignKey(
            entity = FileDataEntity::class,
            parentColumns = arrayOf("key"),
            childColumns = arrayOf("parent"),
            onUpdate = CASCADE,
            onDelete = CASCADE,
        ),
    ],
    indices = [Index("parent", "key", unique = true)]
)
data class FileDataEntity(
    @PrimaryKey
    val key: String,
    val parent: String?,
    val size: Long,
    val lastModified: Instant,
    val type: FileType,
)