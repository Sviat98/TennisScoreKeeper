package com.bashkevich.tennisscorekeeper

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.di.coreModule
import com.bashkevich.tennisscorekeeper.di.counterModule
import com.bashkevich.tennisscorekeeper.di.platformModule
import com.bashkevich.tennisscorekeeper.navigation.CounterDetailsRoute
import com.bashkevich.tennisscorekeeper.navigation.CounterListRoute
import com.bashkevich.tennisscorekeeper.navigation.MatchRoute
import com.bashkevich.tennisscorekeeper.navigation.MatchesRoute
import com.bashkevich.tennisscorekeeper.navigation.platformSpecificRoutes
import com.bashkevich.tennisscorekeeper.screens.MatchScreen
import com.bashkevich.tennisscorekeeper.screens.MatchesScreen
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
    KoinMultiplatformApplication(config = KoinConfiguration { modules(coreModule,counterModule, platformModule) }) {
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
                        }
                    )
                }
                composable<CounterDetailsRoute> {
                    val counterDetailsViewModel = koinViewModel<CounterDetailsViewModel>()

                    CounterDetailsScreen(viewModel = counterDetailsViewModel)
                }
                platformSpecificRoutes()
            }
        }
    }

}