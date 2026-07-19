package com.bashkevich.tennisscorekeeper.core.local

import androidx.datastore.core.Storage
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesSerializer
import com.bashkevich.tennisscorekeeper.core.PlatformConfiguration
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

private const val DATASTORE_FILE_NAME = "tennis_score_keeper.preferences_pb"
private const val APP_DIR = ".tennis-score-keeper"

actual fun createPreferencesStorage(platformConfiguration: PlatformConfiguration): Storage<Preferences> {
    val dir = "${System.getProperty("user.home")}/$APP_DIR"
    FileSystem.SYSTEM.createDirectories(dir.toPath())
    val path: Path = "$dir/$DATASTORE_FILE_NAME".toPath()
    return OkioStorage(
        fileSystem = FileSystem.SYSTEM,
        serializer = PreferencesSerializer,
        producePath = { path }
    )
}
