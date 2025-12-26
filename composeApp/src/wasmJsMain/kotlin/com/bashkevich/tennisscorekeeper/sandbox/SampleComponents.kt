package com.bashkevich.tennisscorekeeper.sandbox

import androidx.compose.foundation.background
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PointerButton(modifier: Modifier = Modifier) {
    var active by remember { mutableStateOf(false) }

    val backgroundColor = if (active) Color.Green else Color.Blue


    Button(onClick = {}, modifier = Modifier.background(backgroundColor).onPointerEvent(PointerEventType.Enter){
        active = true
    }){


    }
}

//fun copyText(){
//    window.navigator.clipboard.
//}