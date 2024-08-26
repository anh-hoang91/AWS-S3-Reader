package com.anhhoang.aws.core.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.anhhoang.aws.feature.s3reader.api.local.FileConverters
import com.anhhoang.aws.feature.s3reader.api.local.FileDataDao
import com.anhhoang.aws.feature.s3reader.api.local.FileDataEntity

/**
 * Local database of the application.
 *
 * The core:local module is the only core module that depends on features api modules to have access
 * to Entities and DAOs. This is due to the limitation of Room database, that all entities and DAOs
 * needs to be declared within the Database. Current architecture design allows separation of
 * entities and DAOs by features, therefore dependency rule is broken and have the core module
 * depends on API modules.
 *
 * Basically this module's only job is to provide the database instance, DAO instances to the
 * dependency injection graph and run migrations if there are any. DAOs will be exposed by the
 * Database, but to use them effectively dependency on the specific api module is needed and this
 * will give food for thought if that DAO is really needed, if yes then should any refactor needed
 * on the api module so that unnecessary classes/implementations are not exposed.
 *
 * Other option is each feature have its own database implementation, however this approach will
 * make the app as if it's using NoSQL.
 */
@Database(
    entities = [FileDataEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(FileConverters::class)
abstract class AwsStorageDatabase : RoomDatabase() {
    abstract fun fileDataDao(): FileDataDao
}
