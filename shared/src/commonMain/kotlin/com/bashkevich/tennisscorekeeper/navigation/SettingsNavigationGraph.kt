package com.bashkevich.tennisscorekeeper.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.bashkevich.tennisscorekeeper.screens.settings.addtheme.AddThemeScreen
import com.bashkevich.tennisscorekeeper.screens.settings.addtheme.AddThemeViewModel
import com.bashkevich.tennisscorekeeper.screens.settings.general.GeneralSettingsScreen
import com.bashkevich.tennisscorekeeper.screens.settings.general.GeneralSettingsViewModel
import com.bashkevich.tennisscorekeeper.screens.settings.generatetheme.GenerateThemeScreen
import com.bashkevich.tennisscorekeeper.screens.settings.generatetheme.GenerateThemeViewModel
import com.bashkevich.tennisscorekeeper.screens.settings.main.SettingsScreen
import com.bashkevich.tennisscorekeeper.screens.settings.main.SettingsViewModel
import com.bashkevich.tennisscorekeeper.screens.settings.themedetails.ScoreboardThemeDetailsScreen
import com.bashkevich.tennisscorekeeper.screens.settings.themedetails.ScoreboardThemeDetailsViewModel
import com.bashkevich.tennisscorekeeper.screens.settings.themelist.ScoreboardThemeListScreen
import com.bashkevich.tennisscorekeeper.screens.settings.themelist.ScoreboardThemeListViewModel
import com.bashkevich.tennisscorekeeper.screens.settings.settemplatelist.SetTemplateListScreen
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.settingsFlow() {
    navigation<SettingsFlowRoute>(startDestination = SettingsRoute) {
        composable<SettingsRoute> {
            val settingsViewModel = koinViewModel<SettingsViewModel>()

            SettingsScreen(viewModel = settingsViewModel)
        }
        composable<GeneralSettingsRoute> {
            val generalSettingsViewModel = koinViewModel<GeneralSettingsViewModel>()

            GeneralSettingsScreen(viewModel = generalSettingsViewModel)
        }
        composable<ScoreboardThemeListRoute> {
            val themeListViewModel = koinViewModel<ScoreboardThemeListViewModel>()

            ScoreboardThemeListScreen(viewModel = themeListViewModel)
        }
        composable<ScoreboardThemeDetailsRoute> {
            val themeDetailsViewModel = koinViewModel<ScoreboardThemeDetailsViewModel>()

            ScoreboardThemeDetailsScreen(viewModel = themeDetailsViewModel)
        }
        composable<AddThemeRoute> {
            val addThemeViewModel = koinViewModel<AddThemeViewModel>()

            AddThemeScreen(viewModel = addThemeViewModel)
        }
        composable<GenerateThemeRoute> {
            val generateThemeViewModel = koinViewModel<GenerateThemeViewModel>()

            GenerateThemeScreen(viewModel = generateThemeViewModel)
        }
        composable<SetTemplateListRoute> {
            SetTemplateListScreen()
        }
    }
}
