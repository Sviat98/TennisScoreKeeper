package com.bashkevich.tennisscorekeeper.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

actual fun NavGraphBuilder.platformSpecificRoutes(){
    composable<MatchRoute>{

    }
}