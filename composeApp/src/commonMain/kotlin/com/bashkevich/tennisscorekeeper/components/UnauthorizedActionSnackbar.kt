package com.bashkevich.tennisscorekeeper.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.navigation.NavController
import com.bashkevich.tennisscorekeeper.navigation.LoginRoute
import org.jetbrains.compose.resources.getString
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.log_in
import tennisscorekeeper.composeapp.generated.resources.you_need_to_log_in

suspend fun SnackbarHostState.showUnauthorizedActionSnackbar(
    navController: NavController,
){
    val result = this.showSnackbar(
        message = getString(Res.string.you_need_to_log_in),
        actionLabel = getString(Res.string.log_in),
        duration = SnackbarDuration.Long
    )

    if (result == SnackbarResult.ActionPerformed) {
        navController.navigate(LoginRoute)
    }
}
