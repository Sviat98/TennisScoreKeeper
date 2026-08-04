package com.bashkevich.tennisscorekeeper.model.settings.domain

/**
 * Languages offered in the switcher. [tag] is the BCP-47 language tag passed to the platform
 * locale; it must match a `values-<tag>` resource directory that exists under composeResources.
 */
enum class AppLanguage(val tag: String) {
    ENGLISH("en"),
    RUSSIAN("ru");

    companion object {
        fun fromName(name: String?): AppLanguage =
            entries.firstOrNull { it.name == name } ?: ENGLISH
    }
}
