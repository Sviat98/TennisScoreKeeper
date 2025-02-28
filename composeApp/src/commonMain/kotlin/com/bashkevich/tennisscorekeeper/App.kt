package com.bashkevich.tennisscorekeeper

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.di.coreModule
import com.bashkevich.tennisscorekeeper.di.counterModule
import com.bashkevich.tennisscorekeeper.di.platformModule
import com.bashkevich.tennisscorekeeper.navigation.AddCounterDialogRoute
import com.bashkevich.tennisscorekeeper.navigation.CounterDetailsRoute
import com.bashkevich.tennisscorekeeper.navigation.CounterListRoute
import com.bashkevich.tennisscorekeeper.navigation.MatchRoute
import com.bashkevich.tennisscorekeeper.navigation.MatchesRoute
import com.bashkevich.tennisscorekeeper.navigation.platformSpecificRoutes
import com.bashkevich.tennisscorekeeper.screens.MatchScreen
import com.bashkevich.tennisscorekeeper.screens.MatchesScreen
import com.bashkevich.tennisscorekeeper.screens.addcounterdialog.AddCounterDialogScreen
import com.bashkevich.tennisscorekeeper.screens.addcounterdialog.AddCounterDialogViewModel
import com.bashkevich.tennisscorekeeper.screens.counterdetails.CounterDetailsScreen
import com.bashkevich.tennisscorekeeper.screens.counterdetails.CounterDetailsViewModel
import com.bashkevich.tennisscorekeeper.screens.counterlist.CounterListScreen
import com.bashkevich.tennisscorekeeper.screens.counterlist.CounterListViewModel
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
            platformModule
        )
    }) {
        MaterialTheme {
            NavHost(navController = navController, startDestination = CounterListRoute) {
                composable<MatchesRoute> {
                    MatchesScreen(
                        onNavigateToMatchRoute = { navController.navigate(MatchRoute(1)) }
                    )
                }
                composable<MatchRoute> {
                    val id = it.toRoute<MatchRoute>().id

                    MatchScreen(matchId = id)
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
                dialog<AddCounterDialogRoute>(
                ) {
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