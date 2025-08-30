package com.bashkevich.tennisscorekeeper

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.bashkevich.tennisscorekeeper.di.authModule
import com.bashkevich.tennisscorekeeper.di.coreModule
import com.bashkevich.tennisscorekeeper.di.matchModule
import com.bashkevich.tennisscorekeeper.di.participantModule
import com.bashkevich.tennisscorekeeper.di.platformModule
import com.bashkevich.tennisscorekeeper.di.setTemplateModule
import com.bashkevich.tennisscorekeeper.di.tournamentModule
import com.bashkevich.tennisscorekeeper.navigation.AddMatchRoute
import com.bashkevich.tennisscorekeeper.navigation.AddTournamentRoute
import com.bashkevich.tennisscorekeeper.navigation.LoginRoute
import com.bashkevich.tennisscorekeeper.navigation.MatchDetailsRoute
import com.bashkevich.tennisscorekeeper.navigation.ProfileRoute
import com.bashkevich.tennisscorekeeper.navigation.TournamentRoute
import com.bashkevich.tennisscorekeeper.navigation.TournamentsRoute
import com.bashkevich.tennisscorekeeper.navigation.platformSpecificRoutes
import com.bashkevich.tennisscorekeeper.screens.addmatch.AddMatchScreen
import com.bashkevich.tennisscorekeeper.screens.addmatch.AddMatchViewModel
import com.bashkevich.tennisscorekeeper.screens.addtournament.AddTournamentScreen
import com.bashkevich.tennisscorekeeper.screens.addtournament.AddTournamentViewModel
import com.bashkevich.tennisscorekeeper.screens.login.LoginScreen
import com.bashkevich.tennisscorekeeper.screens.login.LoginViewModel
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsScreen
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsViewModel
import com.bashkevich.tennisscorekeeper.screens.profile.ProfileScreen
import com.bashkevich.tennisscorekeeper.screens.profile.ProfileViewModel
import com.bashkevich.tennisscorekeeper.screens.tournamentdetails.TournamentScreen
import com.bashkevich.tennisscorekeeper.screens.tournamentdetails.TournamentViewModel
import com.bashkevich.tennisscorekeeper.screens.tournamentlist.TournamentListScreen
import com.bashkevich.tennisscorekeeper.screens.tournamentlist.TournamentListViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinMultiplatformApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.dsl.KoinConfiguration

val LocalNavHostController = staticCompositionLocalOf<NavHostController> {
    error("NavController not provided!")
}

val LocalAuthorization = staticCompositionLocalOf {
    false
}

@OptIn(KoinExperimentalAPI::class)
@Composable
@Preview
fun App(navController: NavHostController = rememberNavController()) {
    KoinMultiplatformApplication(config = KoinConfiguration {
        modules(
            coreModule,
            platformModule,
            tournamentModule,
            matchModule,
            setTemplateModule,
            participantModule,
            authModule
            //fileModule
        )
    }) {
        val appViewModel = koinViewModel<AppViewModel>()

        val appState = appViewModel.state.collectAsStateWithLifecycle()

        val isAuthorized = appState.value.isAuthorized
        CompositionLocalProvider(
            LocalNavHostController provides navController,
            LocalAuthorization provides isAuthorized
        ) {
            MaterialTheme {
                NavHost(navController = navController, startDestination = TournamentsRoute) {
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
                    dialog<AddTournamentRoute> {
                        val addTournamentViewModel = koinViewModel<AddTournamentViewModel>()

                        AddTournamentScreen(
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
                    composable<ProfileRoute> {
                        val profileViewModel = koinViewModel<ProfileViewModel>()

                        ProfileScreen(viewModel = profileViewModel)
                    }
                    composable<AddMatchRoute> {
                        val addMatchViewModel = koinViewModel<AddMatchViewModel>()

                        AddMatchScreen(
                            modifier = Modifier.fillMaxSize(),
                            viewModel = addMatchViewModel
                        )
                    }
                    platformSpecificRoutes()
                }
            }
        }
    }
}