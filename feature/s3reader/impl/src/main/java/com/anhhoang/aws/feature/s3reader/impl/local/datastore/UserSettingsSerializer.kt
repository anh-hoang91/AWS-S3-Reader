package com.anhhoang.aws.feature.s3reader.impl.local.datastore

import androidx.datastore.core.Serializer
import com.anhhoang.aws.feature.s3reader.impl.common.CryptoService
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

/** Serializer for [UserSettings] objects stored in DataStore. */
@Singleton
internal class UserSettingsSerializer @Inject constructor() :
    Serializer<UserSettings> {

    override val defaultValue: UserSettings
        get() = UserSettings()

    override suspend fun readFrom(input: InputStream): UserSettings = try {
        val decrypted = CryptoService.decrypt(input)
        Json.decodeFromString(
            deserializer = UserSettings.serializer(),
            string = decrypted.decodeToString()
        )
    } catch (e: SerializationException) {
        defaultValue
    }

    override suspend fun writeTo(t: UserSettings, output: OutputStream) {
        CryptoService.encrypt(
            bytes = Json.encodeToString(
                serializer = UserSettings.serializer(),
                value = t
            ).encodeToByteArray(),
            outputStream = output,
        )
    }
}