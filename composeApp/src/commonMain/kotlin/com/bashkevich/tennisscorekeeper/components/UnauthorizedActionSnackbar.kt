package com.bashkevich.tennisscorekeeper.components

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.navigation.NavController
import com.bashkevich.tennisscorekeeper.navigation.LoginRoute
import org.jetbrains.compose.resources.getString
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.login
import tennisscorekeeper.composeapp.generated.resources.you_need_to_login

suspend fun SnackbarHostState.showUnauthorizedActionSnackbar(
    navController: NavController,
){
    val result = this.showSnackbar(
        message = getString(Res.string.you_need_to_login),
        actionLabel = getString(Res.string.login),
        duration = SnackbarDuration.Long
    )

    if (result == SnackbarResult.ActionPerformed) {
        navController.navigate(LoginRoute)
    }
}
