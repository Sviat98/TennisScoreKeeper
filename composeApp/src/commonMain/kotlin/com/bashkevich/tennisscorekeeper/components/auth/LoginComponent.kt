package com.bashkevich.tennisscorekeeper.components.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoginComponent(
    modifier: Modifier = Modifier,
    onLoginChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: ()-> Unit
) {
    Column(
        modifier = Modifier.then(modifier),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val loginTextFieldState = rememberTextFieldState()

        LaunchedEffect(loginTextFieldState) {
            snapshotFlow { loginTextFieldState.text.trim().toString() }.collect { login ->
                onLoginChange(login)
            }
        }
        TextField(
            state = loginTextFieldState
        )

        val passwordTextFieldState = rememberTextFieldState()

        LaunchedEffect(passwordTextFieldState) {
            snapshotFlow { passwordTextFieldState.text.trim().toString() }.collect { password ->
                onPasswordChange(password)
            }
        }
        TextField(
            state = passwordTextFieldState,
        )

        Button(onClick = onLoginClick) {
            Text("Login")
        }
    }
}