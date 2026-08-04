package com.bashkevich.tennisscorekeeper.components.environment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue

/**
 * Workaround for runtime locale override per
 * https://kotlinlang.org/docs/multiplatform/compose-resource-environment.html
 *
 * There is no public Compose API to switch the resource locale in Compose Multiplatform 1.11.1
 * (`ComposeEnvironment`/`ResourceEnvironment` are internal), so each platform's `actual` rewrites
 * the platform locale source itself. `provides(tag)` makes `stringResource(Res.string.*)` resolve
 * from the matching `values-<tag>` directory. The caller must wrap the subtree in `key(tag) { }`,
 * because `stringResource` reads `Locale.current` as a non-State read and would not refresh
 * otherwise.
 */
expect object LocalAppLocale {
    val current: String
        @Composable get

    @Composable
    infix fun provides(value: String): ProvidedValue<*>
}
