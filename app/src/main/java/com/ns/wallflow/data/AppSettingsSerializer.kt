package com.ns.wallflow.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.ns.wallflow.model.AppSettingsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.protobuf.ProtoBuf
import java.io.InputStream
import java.io.OutputStream

val Context.settingsDataStore: DataStore<AppSettingsState> by dataStore(
    fileName = "app_settings.pb",
    serializer = AppSettingsSerializer
)

@OptIn(ExperimentalSerializationApi::class)
object AppSettingsSerializer : Serializer<AppSettingsState> {
    override val defaultValue: AppSettingsState = AppSettingsState()

    override suspend fun readFrom(input: InputStream): AppSettingsState {
        return try {
            ProtoBuf.decodeFromByteArray(AppSettingsState.serializer(), input.readBytes())
        } catch (exception: SerializationException) {
            exception.printStackTrace()
            defaultValue
        }
    }

    override suspend fun writeTo(t: AppSettingsState, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(ProtoBuf.encodeToByteArray(AppSettingsState.serializer(), t))
        }
    }
}
