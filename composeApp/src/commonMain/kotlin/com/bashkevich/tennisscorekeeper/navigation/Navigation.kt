package com.bashkevich.tennisscorekeeper.navigation

import androidx.navigation.NavGraphBuilder
import kotlinx.serialization.Serializable

const val COUNTERS_ROUTE_STRING = "counters"
const val COUNTER_OVERLAY_ROUTE_STRING = "counterOverlay"

@Serializable
data object MatchesRoute

@Serializable
data class MatchRoute(val id: Int)

@Serializable
data object CounterListRoute

@Serializable
data object AddCounterDialogRoute

@Serializable
data class CounterDetailsRoute(val id: String)

expect fun NavGraphBuilder.platformSpecificRoutes()