package com.bashkevich.tennisscorekeeper.components.environment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue

/**
 * Workaround for runtime theme override per
 * https://kotlinlang.org/docs/multiplatform/compose-resource-environment.html
 *
 * `provides(value)` overrides the platform system theme so that
 * `isSystemInDarkTheme()` (and therefore [current]) returns the user's choice across
 * the whole wrapped subtree, including third-party code. `null` = follow the system.
 */
expect object LocalAppTheme {
    val current: Boolean
        @Composable get

    @Composable
    infix fun provides(value: Boolean?): ProvidedValue<*>
}
