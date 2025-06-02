package com.bashkevich.tennisscorekeeper.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.bashkevich.tennisscorekeeper.screens.counteroverlay.CounterOverlayScreen
import com.bashkevich.tennisscorekeeper.screens.counteroverlay.CounterOverlayViewModel
import com.bashkevich.tennisscorekeeper.screens.scoreboard.ScoreboardScreen
import com.bashkevich.tennisscorekeeper.screens.scoreboard.ScoreboardViewModel
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

val uri = "http://localhost:8081"

@Serializable
@SerialName("scoreboard")
data class ScoreboardRoute(
    val matchId: String = ""
)

@Serializable
data class CounterOverlayRoute(
    val counterId: String = ""
)

actual fun NavGraphBuilder.platformSpecificRoutes(){
    composable<ScoreboardRoute>{
        val scoreboardViewModel = koinViewModel<ScoreboardViewModel>()
        ScoreboardScreen(viewModel = scoreboardViewModel)
    }
    composable<CounterOverlayRoute>{
        val counterOverlayViewModel = koinViewModel<CounterOverlayViewModel>()
        CounterOverlayScreen(viewModel = counterOverlayViewModel)
    }
}

