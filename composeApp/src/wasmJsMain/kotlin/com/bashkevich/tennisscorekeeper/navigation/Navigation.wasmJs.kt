package com.bashkevich.tennisscorekeeper.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.bashkevich.tennisscorekeeper.screens.scoreboard.ScoreboardScreen
import com.bashkevich.tennisscorekeeper.screens.scoreboard.ScoreboardViewModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
@SerialName("scoreboard")
data class ScoreboardRoute(
    val matchId: String = ""
)

actual fun NavGraphBuilder.platformSpecificRoutes(){
    composable<ScoreboardRoute>{
        val scoreboardViewModel = koinViewModel<ScoreboardViewModel>()
        ScoreboardScreen(viewModel = scoreboardViewModel)
    }
}

