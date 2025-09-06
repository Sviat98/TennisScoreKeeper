package com.bashkevich.tennisscorekeeper.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.LocalAuthorization
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.LoginAppBar
import com.bashkevich.tennisscorekeeper.components.auth.LoginComponent

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.actions.collect { action ->
        }
    }
    LoginScreenContent(
        modifier = Modifier.then(modifier),
        state = state,
        onEvent = { viewModel.onEvent(it) }
    )
}

@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    state: LoginState,
    onEvent: (LoginUiEvent) -> Unit
) {
    val navController = LocalNavHostController.current

    val authorization = LocalAuthorization.current

    LaunchedEffect(authorization){
        if (authorization){
            navController.navigateUp()
        }
    }

    Scaffold(
        modifier = Modifier.then(modifier),
        topBar = { LoginAppBar(onBack = { navController.navigateUp() }) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginComponent(
                onLoginChange = { onEvent(LoginUiEvent.ChangeLogin(it)) },
                onPasswordChange = { onEvent(LoginUiEvent.ChangePassword(it)) },
                onLoginClick = {
                    onEvent(LoginUiEvent.Login)
                }
            )
        }
    }
}