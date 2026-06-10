package com.bashkevich.tennisscorekeeper.core.local

import androidx.room3.RoomDatabase
import com.bashkevich.tennisscorekeeper.core.PlatformConfiguration

expect fun getDatabaseBuilder(
    platformConfiguration: PlatformConfiguration
): RoomDatabase.Builder<AppDatabase>
