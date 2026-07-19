package com.bashkevich.tennisscorekeeper.core.local

import androidx.datastore.core.Storage
import androidx.datastore.preferences.core.Preferences
import com.bashkevich.tennisscorekeeper.core.PlatformConfiguration

expect fun createPreferencesStorage(platformConfiguration: PlatformConfiguration): Storage<Preferences>
