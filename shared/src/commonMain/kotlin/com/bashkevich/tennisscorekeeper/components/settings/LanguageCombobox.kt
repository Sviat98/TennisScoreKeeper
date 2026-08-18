package com.bashkevich.tennisscorekeeper.components.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowDropDown
import com.bashkevich.tennisscorekeeper.model.settings.domain.AppLanguage
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.app_language
import tennisscorekeeper.shared.generated.resources.language_english
import tennisscorekeeper.shared.generated.resources.language_russian
import tennisscorekeeper.shared.generated.resources.open_dropdown

/**
 * Read-only language picker following the project's combobox convention
 * (`Box { TextField(readOnly) + DropdownMenu }`, see [TournamentTypeCombobox]). English is the
 * default — there is no "system" option.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageCombobox(
    modifier: Modifier = Modifier,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val options = AppLanguage.entries

    val languageText = languageLabel(currentLanguage)
    val languageState = TextFieldState(languageText)

    Box(modifier = modifier) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            state = languageState,
            placeholder = { Text(stringResource(Res.string.app_language)) },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        imageVector = IconGroup.Default.ArrowDropDown,
                        contentDescription = stringResource(Res.string.open_dropdown),
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                disabledIndicatorColor = Color.Transparent,
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = languageLabel(option)) },
                    onClick = {
                        onLanguageChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun languageLabel(language: AppLanguage): String = when (language) {
    AppLanguage.ENGLISH -> stringResource(Res.string.language_english)
    AppLanguage.RUSSIAN -> stringResource(Res.string.language_russian)
}
