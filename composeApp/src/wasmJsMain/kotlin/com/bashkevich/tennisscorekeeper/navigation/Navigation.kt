package com.bashkevich.tennisscorekeeper.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.bashkevich.tennisscorekeeper.screens.ScoreboardScreen
import com.bashkevich.tennisscorekeeper.screens.counteroverlay.CounterOverlayScreen
import com.bashkevich.tennisscorekeeper.screens.counteroverlay.CounterOverlayViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

val uri = "http://localhost:8081"

//@Serializable
//data class ScoreboardRoute(val id: Int)

@Serializable
data object ScoreboardRoute

@Serializable
data class CounterOverlayRoute(
    val counterId: String = ""
)

actual fun NavGraphBuilder.platformSpecificRoutes(){
    composable<ScoreboardRoute>{
        ScoreboardScreen(id = 1)
    }
    composable<CounterOverlayRoute>{
        val counterOverlayViewModel = koinViewModel<CounterOverlayViewModel>()
        CounterOverlayScreen(viewModel = counterOverlayViewModel)
    }
}

