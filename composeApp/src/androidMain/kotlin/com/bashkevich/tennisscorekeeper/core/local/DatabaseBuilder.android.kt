package com.bashkevich.tennisscorekeeper.core.local

import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.bashkevich.tennisscorekeeper.core.PlatformConfiguration

actual fun getDatabaseBuilder(
    platformConfiguration: PlatformConfiguration
): RoomDatabase.Builder<AppDatabase> {
    val context = platformConfiguration.androidContext
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("tennis_room.db")

    return Room.databaseBuilder<AppDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    ).setDriver(BundledSQLiteDriver())
}
