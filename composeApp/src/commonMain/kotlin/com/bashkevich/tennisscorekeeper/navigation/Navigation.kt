package com.bashkevich.tennisscorekeeper.navigation

import androidx.navigation.NavGraphBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

const val COUNTERS_ROUTE_STRING = "counters"
const val COUNTER_OVERLAY_ROUTE_STRING = "counterOverlay"

@Serializable
@SerialName("tournaments")
data object TournamentsRoute

@Serializable
@SerialName("tournaments")
data class TournamentRoute(val tournamentId: String)

@Serializable
@SerialName("addTournament")
//@SerialName("tournaments/add") распознает как TournamentRoute(id=add) на НЕ-Андроид
data object AddTournamentRoute

@Serializable
@SerialName("matches")
data class MatchDetailsRoute(val id: String)

@Serializable
@SerialName("addMatch")
//@SerialName("matches/add") распознает как MatchDetailsRoute(id=add) на НЕ-Андроид
data class AddMatchRoute(val tournamentId: String = "")

@Serializable
data object CounterListRoute

@Serializable
data object AddCounterDialogRoute

@Serializable
data class CounterDetailsRoute(val id: String)

expect fun NavGraphBuilder.platformSpecificRoutes()