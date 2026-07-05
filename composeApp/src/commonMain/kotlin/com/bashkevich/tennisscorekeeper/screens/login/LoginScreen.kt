package com.bashkevich.tennisscorekeeper.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bashkevich.tennisscorekeeper.LocalAuthorization
import com.bashkevich.tennisscorekeeper.LocalNavHostController
import com.bashkevich.tennisscorekeeper.components.LoginAppBar
import com.bashkevich.tennisscorekeeper.components.auth.LoginComponent
import com.bashkevich.tennisscorekeeper.mvi.LaunchedUiEffectHandler
import org.jetbrains.compose.resources.getString
import tennisscorekeeper.composeapp.generated.resources.Res
import tennisscorekeeper.composeapp.generated.resources.wrong_login_or_password

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LoginScreenContent(
        modifier = Modifier.then(modifier),
        state = state,
        loginTextFieldState = viewModel.loginTextFieldState,
        passwordTextFieldState = viewModel.passwordTextFieldState,
        onLoginClick = { viewModel.onEvent(LoginUiEvent.Login) },
        onConsumeAction = { viewModel.consumeAction() },
    )
}

@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    state: LoginState,
    loginTextFieldState: TextFieldState,
    passwordTextFieldState: TextFieldState,
    onLoginClick: () -> Unit,
    onConsumeAction: () -> Unit,
) {
    val navController = LocalNavHostController.current

    val authorization = LocalAuthorization.current

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedUiEffectHandler(
        effect = state.action,
        onDismissSnackbar = { snackbarHostState.currentSnackbarData?.dismiss() },
        onConsume = onConsumeAction
    ) { currentAction ->
        when (currentAction) {
            is LoginAction.ShowError ->
                snackbarHostState.showSnackbar(message = currentAction.message)
        }
    }

    LaunchedEffect(authorization) {
        if (authorization) {
            navController.navigateUp()
        }
    }

    val isLoginButtonEnabled =
        loginTextFieldState.text.isNotEmpty() && passwordTextFieldState.text.isNotEmpty() &&
                !state.isLoggingIn

    Scaffold(
        modifier = Modifier.then(modifier),
        topBar = { LoginAppBar(onBack = { navController.navigateUp() }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LoginComponent(
                loginTextFieldState = loginTextFieldState,
                passwordTextFieldState = passwordTextFieldState,
                enabled = isLoginButtonEnabled,
                onLoginClick = onLoginClick
            )
        }
    }
}
