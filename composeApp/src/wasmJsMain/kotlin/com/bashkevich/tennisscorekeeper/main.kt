package com.bashkevich.tennisscorekeeper

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.bindToBrowserNavigation
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.core.findNthOccurrence
import com.bashkevich.tennisscorekeeper.navigation.ScoreboardRoute
import com.bashkevich.tennisscorekeeper.navigation.TournamentRoute
import com.bashkevich.tennisscorekeeper.navigation.TournamentsRoute
import com.bashkevich.tennisscorekeeper.navigation.AddMatchRoute
import com.bashkevich.tennisscorekeeper.navigation.AddTournamentRoute
import com.bashkevich.tennisscorekeeper.navigation.MatchDetailsRoute
import kotlinx.browser.document
import kotlinx.browser.window

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {

    ComposeViewport(document.body!!) {
        App(onNavHostReady = { navController ->
            val initRoute = window.location.hash.substringAfter('#', "")
            println("initRoute = $initRoute")
            when {
                // Identifies the corresponding route and navigates to it
                initRoute.contains("scoreboard") -> {
                    val matchId = initRoute.substring(findNthOccurrence(initRoute,'/',1)+1,findNthOccurrence(initRoute,'/',2))
                    navController.navigate(ScoreboardRoute(matchId = matchId))
                }
                // Identifies the corresponding route and navigates to it
                initRoute.isEmpty()-> {
                    navController.navigate(TournamentsRoute)
                }
                initRoute == "tournaments/add" ->{
                    navController.navigate(AddTournamentRoute)
                }
                initRoute.matches(Regex("tournaments/[0-9]+/addMatch")) ->{
                    val tournamentId = initRoute.substring(findNthOccurrence(initRoute,'/',1)+1,findNthOccurrence(initRoute,'/',2))

                    navController.navigate(AddMatchRoute(tournamentId = tournamentId))
                }

                else -> navController.navigate(initRoute)
            }

            navController.bindToBrowserNavigation()
            { navBackStackEntry ->
                val destination = navBackStackEntry.destination

                val route = navBackStackEntry.destination.route.orEmpty()
                println("route = $route")
                when {
                    destination.hasRoute(ScoreboardRoute::class)->{
                        val matchId = navBackStackEntry.toRoute<ScoreboardRoute>().matchId
                        "#matches/$matchId/scoreboard"
                    }
                    destination.hasRoute(TournamentRoute::class)->{
                        val tournamentId = navBackStackEntry.toRoute<TournamentRoute>().tournamentId

                        "#tournaments/$tournamentId"
                    }
                    destination.hasRoute(AddTournamentRoute::class)->{
                        "#tournaments/add"
                    }
                    destination.hasRoute(MatchDetailsRoute::class)->{
                        val matchId = navBackStackEntry.toRoute<MatchDetailsRoute>().id

                        "#matches/$matchId"
                    }
                    destination.hasRoute(AddMatchRoute::class)->{
                        val tournamentId = navBackStackEntry.toRoute<AddMatchRoute>().tournamentId

                        "#tournaments/$tournamentId/addMatch"
                    }
                    else-> "#$route"
                }
            }
        })
    }

}