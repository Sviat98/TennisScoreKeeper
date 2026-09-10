package com.bashkevich.tennisscorekeeper.components.set_template

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
 * Трейлинг-иконка комбобокса шаблона сетов: стрелка раскрытия списка, при ошибке загрузки
 * выбранного шаблона — кнопка повтора.
 *
 * Ветвление по состоянию обязано жить здесь, внутри именованной composable-функции
 * (см. комментарий в ThemeDropdownMenu.kt): when из этой функции нельзя возвращать
 * лямбды из веток, но сам when внутри функции на Kotlin/Native безопасен.
 */
@Composable
internal fun SetTemplateComboboxTrailingIcon(
    state: SetComponentState.SelectedSetState,
    dropdownEnabled: Boolean,
    onRetrySelectedSet: (Int) -> Unit,
    onExpandDropdown: () -> Unit,
) {
    when (state) {
        is SetComponentState.SelectedSetState.Idle -> {
            IconButton(
                onClick = onExpandDropdown,
                enabled = dropdownEnabled,
            ) {
                Icon(
                    imageVector = IconGroup.Default.ArrowDropDown,
                    contentDescription = stringResource(Res.string.open_dropdown),
                )
            }
        }

        is SetComponentState.SelectedSetState.Loading -> Unit

        is SetComponentState.SelectedSetState.Error -> {
            IconButton(onClick = { onRetrySelectedSet(state.initialSetTemplateId) }) {
                Icon(
                    imageVector = IconGroup.Default.Autorenew,
                    contentDescription = stringResource(Res.string.retry),
                )
            }
        }
    }
}
