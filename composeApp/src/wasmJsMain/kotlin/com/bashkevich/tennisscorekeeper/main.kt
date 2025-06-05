package com.bashkevich.tennisscorekeeper

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.NavHostController
import androidx.navigation.bindToNavigation
import androidx.navigation.compose.rememberNavController
import kotlinx.browser.document
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {

    ComposeViewport(document.body!!) {
        val navController: NavHostController = rememberNavController()

        App(navController = navController)

        LaunchedEffect(Unit) {
//            window.bindToNavigation(navController){navBackStackEntry->
//                when{
//                    navBackStackEntry.destination.hasRoute(CounterListRoute::class)-> COUNTERS_ROUTE_STRING
//                    navBackStackEntry.destination.hasRoute(CounterDetailsRoute::class) -> {
//                        val counterId = navBackStackEntry.toRoute<CounterDetailsRoute>().id
//
//                        "$COUNTERS_ROUTE_STRING/$counterId"
//                    }
//                    navBackStackEntry.destination.hasRoute(CounterOverlayRoute::class) -> {
//                        val counterId = navBackStackEntry.toRoute<CounterOverlayRoute>().counterId
//
//                        "$COUNTER_OVERLAY_ROUTE_STRING/1"
//                    }
//                    else -> navBackStackEntry.destination.toString()
//                }
//
//            }
            window.bindToNavigation(navController)
        }

    }
}