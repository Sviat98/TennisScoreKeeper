package com.bashkevich.tennisscorekeeper.core.local

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import com.bashkevich.tennisscorekeeper.model.set_template.local.SetTemplateDao
import com.bashkevich.tennisscorekeeper.model.set_template.local.SetTemplateEntity
import com.bashkevich.tennisscorekeeper.model.theme.local.ThemeDao
import com.bashkevich.tennisscorekeeper.model.theme.local.ThemeEntity
import com.bashkevich.tennisscorekeeper.model.tournament.local.TournamentDao
import com.bashkevich.tennisscorekeeper.model.tournament.local.TournamentEntity

@Database(
    entities = [TournamentEntity::class, SetTemplateEntity::class, ThemeEntity::class],
    version = 1
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tournamentDao(): TournamentDao
    abstract fun setTemplateDao(): SetTemplateDao
    abstract fun themeDao(): ThemeDao
}

expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
