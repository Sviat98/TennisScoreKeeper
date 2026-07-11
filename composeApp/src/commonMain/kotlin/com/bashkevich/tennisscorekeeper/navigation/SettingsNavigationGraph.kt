package com.bashkevich.tennisscorekeeper.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.bashkevich.tennisscorekeeper.screens.settings.general.GeneralSettingsScreen
import com.bashkevich.tennisscorekeeper.screens.settings.main.SettingsScreen
import com.bashkevich.tennisscorekeeper.screens.settings.themelist.ScoreboardThemeListScreen
import com.bashkevich.tennisscorekeeper.screens.settings.settemplatelist.SetTemplateListScreen
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.settingsFlow() {
    navigation<SettingsRoute>(startDestination = SettingsRoute) {
        composable<SettingsRoute> {
            val settingsViewModel = koinViewModel<com.bashkevich.tennisscorekeeper.screens.settings.main.SettingsViewModel>()

            SettingsScreen(viewModel = settingsViewModel)
        }
        composable<GeneralSettingsRoute> {
            GeneralSettingsScreen()
        }
        composable<ScoreboardThemeListRoute> {
            ScoreboardThemeListScreen()
        }
        composable<SetTemplateListRoute> {
            SetTemplateListScreen()
        }
    }
}
