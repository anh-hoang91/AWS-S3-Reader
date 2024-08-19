package com.anhhoang.aws.feature.s3reader.impl.data

import com.anhhoang.aws.feature.s3reader.api.data.model.FileData
import com.anhhoang.aws.feature.s3reader.api.local.FileDataEntity

fun FileDataEntity.toModel(): FileData = FileData(
    key = key,
    parent = parent,
    size = size,
    lastModified = lastModified,
    type = type,
)