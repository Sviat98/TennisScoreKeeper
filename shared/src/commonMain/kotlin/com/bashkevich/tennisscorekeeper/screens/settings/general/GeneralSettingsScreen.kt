package com.bashkevich.tennisscorekeeper.screens.settings.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.ArrowBack
import com.bashkevich.tennisscorekeeper.components.settings.LanguageCombobox
import com.bashkevich.tennisscorekeeper.model.settings.domain.AppThemeMode
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.app_language
import tennisscorekeeper.shared.generated.resources.app_theme
import tennisscorekeeper.shared.generated.resources.general_settings
import tennisscorekeeper.shared.generated.resources.navigate_back
import tennisscorekeeper.shared.generated.resources.theme_dark
import tennisscorekeeper.shared.generated.resources.theme_light
import tennisscorekeeper.shared.generated.resources.theme_system

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: GeneralSettingsViewModel,
) {
    val navController = LocalNavHostController.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Keep modes and labels positionally aligned: SYSTEM, LIGHT, DARK.
    val themeModes = AppThemeMode.entries
    val themeLabels = listOf(
        stringResource(Res.string.theme_system),
        stringResource(Res.string.theme_light),
        stringResource(Res.string.theme_dark),
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.general_settings)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = IconGroup.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.navigate_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(Res.string.app_theme),
                style = MaterialTheme.typography.titleMedium,
            )
            // The whole row is NOT clickable — the RadioButton toggles only on a direct
            // click of the button itself. Only the option matching state.appThemeMode is
            // selected, so exactly one is highlighted at any time.
            themeModes.forEachIndexed { index, mode ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = state.appThemeMode == mode,
                        onClick = {
                            // DEBUG: trace theme selection (web console / logcat). Remove later.
                            println("[ThemeSettings] RadioButton.onClick: index=$index mode=$mode current=${state.appThemeMode}")
                            viewModel.onEvent(GeneralSettingsUiEvent.ChangeThemeMode(mode))
                        },
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(themeLabels[index])
                }
            }

            Text(
                text = stringResource(Res.string.app_language),
                style = MaterialTheme.typography.titleMedium,
            )
            // No "System" option — English is the default. The field reflects the saved choice.
            LanguageCombobox(
                currentLanguage = state.appLanguage,
                onLanguageChange = { viewModel.onEvent(GeneralSettingsUiEvent.ChangeLanguage(it)) },
            )
        }
    }
}
