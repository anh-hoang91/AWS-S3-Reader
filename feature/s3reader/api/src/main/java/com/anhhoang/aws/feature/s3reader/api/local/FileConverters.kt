package com.anhhoang.aws.feature.s3reader.api.local

import androidx.room.TypeConverter
import com.anhhoang.aws.feature.s3reader.api.data.model.FileType
import java.time.Instant

class FileConverters {

    @TypeConverter
    fun fromFileType(value: FileType): String = value.name

    @TypeConverter
    fun toFileType(value: String): FileType = FileType.valueOf(value)

    @TypeConverter
    fun fromInstant(value: Instant): Long = value.toEpochMilli()

    @TypeConverter
    fun toInstant(value: Long): Instant = Instant.ofEpochMilli(value)
}