package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bashkevich.tennisscorekeeper.model.counter.Counter
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CounterView(
    modifier: Modifier = Modifier,
    counter: Counter
) {
    Box(
        modifier = Modifier.then(modifier).wrapContentSize().background(
            color = Color.Blue
        ).padding(8.dp)
    ) {
        Text("Current value: ${counter.value}", color = Color.White, fontSize = 18.sp)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun CounterCard(
    modifier: Modifier = Modifier,
    counter: Counter,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.then(modifier),
        border = BorderStroke(1.dp, color = Color.Black),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp,Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(counter.name)
            CounterView(counter = counter)
        }
    }

}