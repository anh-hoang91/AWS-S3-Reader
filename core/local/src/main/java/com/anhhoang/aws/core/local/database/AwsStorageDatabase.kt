package com.anhhoang.aws.core.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Local database of the application.
 */
@Database(
    entities = [],
    version = 1,
    exportSchema = true
)
abstract class AwsStorageDatabase: RoomDatabase()
