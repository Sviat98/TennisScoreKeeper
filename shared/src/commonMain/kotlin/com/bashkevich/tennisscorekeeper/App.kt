package com.bashkevich.tennisscorekeeper

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bashkevich.tennisscorekeeper.components.environment.AppTheme
import com.bashkevich.tennisscorekeeper.components.environment.LocalAppLocale
import com.bashkevich.tennisscorekeeper.components.environment.LocalAppTheme
import com.bashkevich.tennisscorekeeper.di.authModule
import com.bashkevich.tennisscorekeeper.di.coreModule
import com.bashkevich.tennisscorekeeper.di.matchModule
import com.bashkevich.tennisscorekeeper.di.participantModule
import com.bashkevich.tennisscorekeeper.di.platformModule
import com.bashkevich.tennisscorekeeper.di.setTemplateModule
import com.bashkevich.tennisscorekeeper.di.settingsModule
import com.bashkevich.tennisscorekeeper.di.themeModule
import com.bashkevich.tennisscorekeeper.di.tournamentModule
import com.bashkevich.tennisscorekeeper.model.settings.domain.AppLanguage
import com.bashkevich.tennisscorekeeper.model.settings.domain.AppThemeMode
import com.bashkevich.tennisscorekeeper.navigation.AddMatchRoute
import com.bashkevich.tennisscorekeeper.navigation.AddTournamentRoute
import com.bashkevich.tennisscorekeeper.navigation.LoginRoute
import com.bashkevich.tennisscorekeeper.navigation.MatchDetailsRoute
import com.bashkevich.tennisscorekeeper.navigation.TournamentRoute
import com.bashkevich.tennisscorekeeper.navigation.TournamentsRoute
import com.bashkevich.tennisscorekeeper.navigation.platformSpecificRoutes
import com.bashkevich.tennisscorekeeper.navigation.settingsFlow
import com.bashkevich.tennisscorekeeper.screens.addmatch.AddMatchScreen
import com.bashkevich.tennisscorekeeper.screens.addmatch.AddMatchViewModel
import com.bashkevich.tennisscorekeeper.screens.addtournament.AddTournamentScreen
import com.bashkevich.tennisscorekeeper.screens.addtournament.AddTournamentViewModel
import com.bashkevich.tennisscorekeeper.screens.login.LoginScreen
import com.bashkevich.tennisscorekeeper.screens.login.LoginViewModel
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsScreen
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsViewModel
import com.bashkevich.tennisscorekeeper.screens.tournamentdetails.TournamentScreen
import com.bashkevich.tennisscorekeeper.screens.tournamentdetails.TournamentViewModel
import com.bashkevich.tennisscorekeeper.screens.tournamentlist.TournamentListScreen
import com.bashkevich.tennisscorekeeper.screens.tournamentlist.TournamentListViewModel
import org.koin.compose.KoinApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.koinConfiguration

val LocalNavHostController = staticCompositionLocalOf<NavHostController> {
    error("NavController not provided!")
}

val LocalAuthorization = staticCompositionLocalOf {
    false
}

@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App(
    onNavHostReady: suspend (NavController) -> Unit = {}
) {
    KoinApplication(configuration = koinConfiguration {
        modules(
            coreModule,
            platformModule,
            tournamentModule,
            matchModule,
            setTemplateModule,
            themeModule,
            participantModule,
            authModule,
            settingsModule
            //fileModule
        )
    }) {
        val appViewModel = koinViewModel<AppViewModel>()

        val appState = appViewModel.state.collectAsStateWithLifecycle()

        val navController = rememberNavController()

        val isAuthorized = appState.value.isAuthorized
        val themeOverride: Boolean? = when (appState.value.appThemeMode) {
            AppThemeMode.SYSTEM -> null
            AppThemeMode.LIGHT -> false
            AppThemeMode.DARK -> true
        }
        // Locale tag for the Compose resource workaround (see LocalAppLocale). Always non-null —
        // the switcher has no "system" option, so the tag is "en" or "ru".
        val localeTag: String = appState.value.appLanguage.tag
        // DEBUG: trace theme application (web console / logcat). Remove later.
        println("[ThemeSettings] apply: mode=${appState.value.appThemeMode} override=$themeOverride")

        // LocalAppTheme overrides isSystemInDarkTheme() for the whole subtree, so
        // AppTheme's LocalAppTheme.current (and any third-party reader) follows the
        // user's choice. null = follow the system theme.
        //
        // LocalAppLocale rewrites the platform locale so stringResource resolves from the
        // matching values-<tag> dir. key(localeTag) recreates the subtree so every
        // stringResource re-resolves on change (the NavHost back-stack resets — known tradeoff
        // of the workaround; acceptable for a settings-driven change).
        CompositionLocalProvider(
            LocalNavHostController provides navController,
            LocalAuthorization provides isAuthorized,
            LocalAppTheme provides themeOverride,
            LocalAppLocale provides localeTag,
        ) {
            key(localeTag) {
                AppTheme {
                    NavHost(
                        modifier = Modifier
                            .fillMaxSize(),
                        navController = navController,
                        startDestination = TournamentsRoute
                    ) {
                        composable<TournamentsRoute> {
                            val tournamentListViewModel = koinViewModel<TournamentListViewModel>()

                            TournamentListScreen(
                                modifier = Modifier.fillMaxSize(),
                                viewModel = tournamentListViewModel,
                            )
                        }
                        composable<TournamentRoute> {
                            val tournamentViewModel = koinViewModel<TournamentViewModel>()

                            TournamentScreen(
                                modifier = Modifier.fillMaxSize(),
                                viewModel = tournamentViewModel,
                            )
                        }
                        composable<AddTournamentRoute> {
                            val addTournamentViewModel = koinViewModel<AddTournamentViewModel>()

                            AddTournamentScreen(
                                modifier = Modifier.fillMaxSize(),
                                viewModel = addTournamentViewModel,
                            )
                        }
                        composable<MatchDetailsRoute> {
                            val matchDetailsViewModel = koinViewModel<MatchDetailsViewModel>()

                            MatchDetailsScreen(viewModel = matchDetailsViewModel)
                        }
                        composable<LoginRoute> {
                            val loginViewModel = koinViewModel<LoginViewModel>()

                            LoginScreen(viewModel = loginViewModel)
                        }
                        settingsFlow()
                        composable<AddMatchRoute> {
                            val addMatchViewModel = koinViewModel<AddMatchViewModel>()

                            AddMatchScreen(
                                modifier = Modifier.fillMaxSize(),
                                viewModel = addMatchViewModel
                            )
                        }
                        platformSpecificRoutes()
                    }
                    LaunchedEffect(navController) {
                        onNavHostReady(navController)
                    }
                }
            }
        }
    }
}
