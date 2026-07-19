package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SandboxComponent() {
    Row(
        modifier = Modifier
            .height(IntrinsicSize.Min)
            .background(Color.Blue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .width(32.dp)
                .background(Color.Yellow)
        ) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f).background(Color.Red))
            Box(modifier = Modifier.fillMaxWidth().weight(1f).background(Color.Green))
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(vertical = 8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Первый текст", modifier = Modifier.alignByBaseline())
                Box(
                    modifier = Modifier.size(8.dp).clip(
                        CircleShape
                    ).background(color = Color.Yellow).alignByBaseline()
                )
            }
            Spacer(Modifier.height(8.dp))
            Text("Второй текст")
        }
    }
}
