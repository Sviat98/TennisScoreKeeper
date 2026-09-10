package com.bashkevich.tennisscorekeeper.components.theme

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowDropDown
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Autorenew
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.open_dropdown
import tennisscorekeeper.shared.generated.resources.retry

/**
 * Трейлинг-иконка комбобокса темы: стрелка раскрытия списка, при ошибке загрузки выбранной
 * темы — кнопка повтора.
 *
 * Ветвление по состоянию обязано жить здесь, внутри именованной composable-функции:
 * на Kotlin/Native composable-лямбда корректно кодогенерируется только создаваемой в одной
 * точке под одним условием (см. комментарий в ThemeDropdownMenu.kt), поэтому when из этой
 * функции нельзя возвращать лямбды из веток — но сам when внутри функции безопасен.
 */
@Composable
internal fun ThemeComboboxTrailingIcon(
    state: ThemeComponentState.SelectedThemeState,
    onRetrySelectedTheme: (Int) -> Unit,
    onExpandDropdown: () -> Unit,
) {
    when (state) {
        is ThemeComponentState.SelectedThemeState.Idle -> {
            IconButton(onClick = onExpandDropdown) {
                Icon(
                    imageVector = IconGroup.Default.ArrowDropDown,
                    contentDescription = stringResource(Res.string.open_dropdown),
                )
            }
        }

        is ThemeComponentState.SelectedThemeState.Loading -> Unit

        is ThemeComponentState.SelectedThemeState.Error -> {
            IconButton(onClick = { onRetrySelectedTheme(state.initialThemeId) }) {
                Icon(
                    imageVector = IconGroup.Default.Autorenew,
                    contentDescription = stringResource(Res.string.retry),
                )
            }
        }
    }
}
