package com.bashkevich.tennisscorekeeper.components.match

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.bashkevich.tennisscorekeeper.model.match.SimpleMatch
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterialApi::class)
@Composable
@Preview
fun MatchCard(
    modifier: Modifier = Modifier,
    match: SimpleMatch,
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
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MatchItemView(match = match)
        }
    }

}

@Composable
fun MatchItemView(
    modifier: Modifier = Modifier,
    match: SimpleMatch
) {
    Column(
        modifier = Modifier.then(modifier).wrapContentSize().background(
            color = Color.Blue
        ).padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(match.firstPlayer)
        Text(match.status)
        Text(match.secondPlayer)
    }
}