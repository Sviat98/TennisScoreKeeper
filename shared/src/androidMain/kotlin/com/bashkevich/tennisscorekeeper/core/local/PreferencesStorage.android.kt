package com.bashkevich.tennisscorekeeper.core.local

import androidx.datastore.core.Storage
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesSerializer
import com.bashkevich.tennisscorekeeper.core.PlatformConfiguration
import okio.FileSystem
import okio.Path.Companion.toOkioPath

private const val DATASTORE_FILE_NAME = "tennis_score_keeper.preferences_pb"

actual fun createPreferencesStorage(platformConfiguration: PlatformConfiguration): Storage<Preferences> =
    OkioStorage(
        fileSystem = FileSystem.SYSTEM,
        serializer = PreferencesSerializer,
        producePath = { platformConfiguration.androidContext.dataStoreFile(DATASTORE_FILE_NAME).toOkioPath() }
    )
