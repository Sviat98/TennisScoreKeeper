package com.bashkevich.tennisscorekeeper.model.settings.domain

/** App theme options offered in the switcher. */
enum class AppThemeMode {
    SYSTEM, LIGHT, DARK;

    companion object {
        fun fromName(name: String?): AppThemeMode =
            entries.firstOrNull { it.name == name } ?: SYSTEM
    }
}
