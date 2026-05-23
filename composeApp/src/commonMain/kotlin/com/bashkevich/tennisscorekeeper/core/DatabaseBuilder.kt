package com.bashkevich.tennisscorekeeper.core

import androidx.room3.RoomDatabase

expect fun getDatabaseBuilder(
    platformConfiguration: PlatformConfiguration
): RoomDatabase.Builder<AppDatabase>
