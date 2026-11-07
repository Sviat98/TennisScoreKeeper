package com.bashkevich.tennisscorekeeper.components.status

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.model.match.remote.body.MatchStatus
import com.bashkevich.tennisscorekeeper.model.match.remote.body.toResource
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentStatus
import com.bashkevich.tennisscorekeeper.model.tournament.remote.toResource
import org.jetbrains.compose.resources.stringResource

private fun statusColorByName(statusName: String): Color = when (statusName) {
    "NOT_STARTED" -> Color(0xFF9E9E9E)
    "IN_PROGRESS" -> Color(0xFFF44336)
    "PAUSED" -> Color(0xFFFBC02D)
    "COMPLETED" -> Color(0xFF4CAF50)
    else -> Color(0xFF9E9E9E)
}

fun MatchStatus.statusColor(): Color = statusColorByName(name)

fun TournamentStatus.statusColor(): Color = statusColorByName(name)

@Composable
fun StatusText(modifier: Modifier = Modifier, status: MatchStatus) {
    StatusText(
        modifier = modifier,
        text = stringResource(status.toResource()),
        color = status.statusColor()
    )
}

@Composable
fun StatusText(modifier: Modifier = Modifier, status: TournamentStatus) {
    StatusText(
        modifier = modifier,
        text = stringResource(status.toResource()),
        color = status.statusColor()
    )
}

@Composable
private fun StatusText(modifier: Modifier = Modifier, text: String, color: Color) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(text)
    }
}
