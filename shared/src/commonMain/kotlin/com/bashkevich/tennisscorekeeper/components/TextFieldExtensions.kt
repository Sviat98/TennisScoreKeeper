package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.text.input.TextFieldState

fun TextFieldState.updateTextField(text: String) {
    this.edit {
        replace(0, length, text)
    }
}