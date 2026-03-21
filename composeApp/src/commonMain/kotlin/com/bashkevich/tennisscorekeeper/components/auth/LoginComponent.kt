package com.bashkevich.tennisscorekeeper.components.auth

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.SecureTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Visibility
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.VisibilityOff

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
            state = loginTextFieldState,
            label = { Text("Login") }
        )

        val passwordTextFieldState = rememberTextFieldState()

        LaunchedEffect(passwordTextFieldState) {
            snapshotFlow { passwordTextFieldState.text.trim().toString() }.collect { password ->
                onPasswordChange(password)
            }
        }

        PasswordTextField(
            textFieldState = passwordTextFieldState,
        )

        Button(onClick = onLoginClick) {
            Text("Login")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordTextField(
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState,
) {
    var passwordHidden by remember { mutableStateOf(true) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> passwordHidden = false
                is PressInteraction.Release -> passwordHidden = true
                is PressInteraction.Cancel -> passwordHidden = true
            }
        }
    }

    SecureTextField(
        modifier = Modifier.then(modifier),
        state = textFieldState,
        label = { Text("Password") },
        textObfuscationMode =
            if (passwordHidden) TextObfuscationMode.RevealLastTyped
            else TextObfuscationMode.Visible,
        trailingIcon = {
            val description = if (passwordHidden) "Show password" else "Hide password"
            TooltipBox(
                positionProvider =
                    TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
                tooltip = { PlainTooltip { Text(description) } },
                state = rememberTooltipState(),
            ) {
                IconButton(
                    onClick = {},
                    interactionSource = interactionSource
                ) {
                    val visibilityIcon =
                        if (passwordHidden) IconGroup.Default.Visibility else IconGroup.Default.VisibilityOff
                    Icon(imageVector = visibilityIcon, contentDescription = description)
                }
            }
        },
    )
}