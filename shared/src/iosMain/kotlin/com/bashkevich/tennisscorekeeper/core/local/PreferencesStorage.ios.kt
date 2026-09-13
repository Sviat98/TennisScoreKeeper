package com.bashkevich.tennisscorekeeper.core.local

import androidx.datastore.core.Storage
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesSerializer
import com.bashkevich.tennisscorekeeper.core.PlatformConfiguration
import kotlinx.cinterop.ExperimentalForeignApi
import okio.FileSystem
import okio.Path.Companion.toPath
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

private const val DATASTORE_FILE_NAME = "tennis_score_keeper.preferences_pb"

@OptIn(ExperimentalForeignApi::class)
actual fun createPreferencesStorage(platformConfiguration: PlatformConfiguration): Storage<Preferences> {
    val documentDirectory: NSURL? = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    val path = "${requireNotNull(documentDirectory).path}/$DATASTORE_FILE_NAME".toPath()

    return OkioStorage(
        fileSystem = FileSystem.SYSTEM,
        serializer = PreferencesSerializer,
        producePath = { path }
    )
}
