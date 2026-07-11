package com.bashkevich.tennisscorekeeper.navigation

import androidx.navigation.NavGraphBuilder
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("tournaments")
data object TournamentsRoute

@Serializable
@SerialName("tournaments")
data class TournamentRoute(val tournamentId: Int)

@Serializable
@SerialName("addTournament")
//@SerialName("tournaments/add") распознает как TournamentRoute(id=add) на НЕ-Андроид
data object AddTournamentRoute

@Serializable
@SerialName("matches")
data class MatchDetailsRoute(val id: Int)

@Serializable
@SerialName("login")
data object LoginRoute

@Serializable
@SerialName("addMatch")
//@SerialName("matches/add") распознает как MatchDetailsRoute(id=add) на НЕ-Андроид
data class AddMatchRoute(val tournamentId: Int = 0)

@Serializable
@SerialName("settings_flow")
data object SettingsFlowRoute

@Serializable
@SerialName("settings")
data object SettingsRoute

@Serializable
@SerialName("general_settings")
data object GeneralSettingsRoute

@Serializable
@SerialName("themes")
data object ScoreboardThemeListRoute

@Serializable
@SerialName("set_templates")
data object SetTemplateListRoute

expect fun NavGraphBuilder.platformSpecificRoutes()