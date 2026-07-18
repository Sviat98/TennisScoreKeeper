package com.bashkevich.tennisscorekeeper.core.local

import androidx.datastore.core.Storage
import androidx.datastore.core.okio.WebOpfsStorage
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferencesSerializer
import com.bashkevich.tennisscorekeeper.core.PlatformConfiguration

private const val DATASTORE_NAME = "tennis_score_keeper.preferences_pb"

actual fun createPreferencesStorage(platformConfiguration: PlatformConfiguration): Storage<Preferences> {
    println(">>> createPreferencesStorage: creating WebOpfsStorage (OPFS)")
    return WebOpfsStorage(
        serializer = PreferencesSerializer,
        name = DATASTORE_NAME
    )
}
