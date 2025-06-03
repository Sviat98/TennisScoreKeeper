package com.bashkevich.tennisscorekeeper

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.di.coreModule
import com.bashkevich.tennisscorekeeper.di.counterModule
import com.bashkevich.tennisscorekeeper.di.matchModule
import com.bashkevich.tennisscorekeeper.di.platformModule
import com.bashkevich.tennisscorekeeper.di.tournamentModule
import com.bashkevich.tennisscorekeeper.navigation.AddCounterDialogRoute
import com.bashkevich.tennisscorekeeper.navigation.AddTournamentRoute
import com.bashkevich.tennisscorekeeper.navigation.CounterDetailsRoute
import com.bashkevich.tennisscorekeeper.navigation.CounterListRoute
import com.bashkevich.tennisscorekeeper.navigation.MatchDetailsRoute
import com.bashkevich.tennisscorekeeper.navigation.MatchesRoute
import com.bashkevich.tennisscorekeeper.navigation.TournamentRoute
import com.bashkevich.tennisscorekeeper.navigation.TournamentTab
import com.bashkevich.tennisscorekeeper.navigation.TournamentsRoute
import com.bashkevich.tennisscorekeeper.navigation.platformSpecificRoutes
import com.bashkevich.tennisscorekeeper.screens.addcounterdialog.AddCounterDialogScreen
import com.bashkevich.tennisscorekeeper.screens.addcounterdialog.AddCounterDialogViewModel
import com.bashkevich.tennisscorekeeper.screens.addtournament.AddTournamentScreen
import com.bashkevich.tennisscorekeeper.screens.addtournament.AddTournamentViewModel
import com.bashkevich.tennisscorekeeper.screens.counterdetails.CounterDetailsScreen
import com.bashkevich.tennisscorekeeper.screens.counterdetails.CounterDetailsViewModel
import com.bashkevich.tennisscorekeeper.screens.counterlist.CounterListScreen
import com.bashkevich.tennisscorekeeper.screens.counterlist.CounterListViewModel
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsScreen
import com.bashkevich.tennisscorekeeper.screens.matchdetails.MatchDetailsViewModel
import com.bashkevich.tennisscorekeeper.screens.matchlist.MatchListScreen
import com.bashkevich.tennisscorekeeper.screens.matchlist.MatchListViewModel
import com.bashkevich.tennisscorekeeper.screens.tournamentdetails.TournamentScreen
import com.bashkevich.tennisscorekeeper.screens.tournamentdetails.TournamentViewModel
import com.bashkevich.tennisscorekeeper.screens.tournamentlist.TournamentListScreen
import com.bashkevich.tennisscorekeeper.screens.tournamentlist.TournamentListViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinMultiplatformApplication
import org.koin.compose.viewmodel.koinViewModel
import org.koin.dsl.KoinConfiguration

@Composable
@Preview
fun App(navController: NavHostController = rememberNavController()) {
    KoinMultiplatformApplication(config = KoinConfiguration {
        modules(
            coreModule,
            counterModule,
            matchModule,
            tournamentModule,
            platformModule
        )
    }) {
        MaterialTheme {
            NavHost(navController = navController, startDestination = TournamentsRoute) {
                composable<TournamentsRoute> {
                    val tournamentListViewModel = koinViewModel<TournamentListViewModel>()

                    TournamentListScreen(
                        viewModel = tournamentListViewModel,
                        onTournamentAdd = { navController.navigate(AddTournamentRoute) },
                        onTournamentClick = { tournament ->
                            navController.navigate(
                                TournamentRoute.create(
                                    tournament.id,
                                    TournamentTab.Matches
                                )
                            )
                        }
                    )
                }
                composable<TournamentRoute> {
                    val tournamentViewModel = koinViewModel<TournamentViewModel>()

                    val tournamentId = it.toRoute<TournamentRoute>().tournamentId

                    TournamentScreen(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = tournamentViewModel,
                        onTabSelected = { currentTab ->
                            navController.navigate(
                                TournamentRoute.create(
                                    tournamentId,
                                    currentTab
                                )
                            )
                        }
                    )
                }
                dialog<AddTournamentRoute>(

                ) {
                    val addTournamentViewModel = koinViewModel<AddTournamentViewModel>()

                    AddTournamentScreen(
                        viewModel = addTournamentViewModel,
                        onDismissRequest = { navController.navigateUp() }
                    )
                }
                composable<MatchesRoute> {
                    val matchListViewModel = koinViewModel<MatchListViewModel>()

                    MatchListScreen(
                        viewModel = matchListViewModel,
                        onMatchClick = { match ->
                            navController.navigate(
                                MatchDetailsRoute(
                                    match.id
                                )
                            )
                        },
                        onMatchAdd = {}
                    )
                }
                composable<MatchDetailsRoute> {
                    val matchDetailsViewModel = koinViewModel<MatchDetailsViewModel>()

                    MatchDetailsScreen(viewModel = matchDetailsViewModel)
                }
                composable<CounterListRoute> {
                    val counterListViewModel = koinViewModel<CounterListViewModel>()
                    CounterListScreen(
                        viewModel = counterListViewModel,
                        onCounterClick = { counter ->
                            navController.navigate(
                                CounterDetailsRoute(
                                    counter.id
                                )
                            )
                        },
                        onCounterAdd = {
                            navController.navigate(AddCounterDialogRoute)
                        }
                    )
                }
                composable<CounterDetailsRoute> {
                    val counterDetailsViewModel = koinViewModel<CounterDetailsViewModel>()

                    CounterDetailsScreen(viewModel = counterDetailsViewModel)
                }
                dialog<AddCounterDialogRoute> {
                    val addCounterDialogViewModel = koinViewModel<AddCounterDialogViewModel>()

                    AddCounterDialogScreen(
                        viewModel = addCounterDialogViewModel,
                        onDismissRequest = { navController.navigateUp() })
                }
                platformSpecificRoutes()
            }
        }
    }
}