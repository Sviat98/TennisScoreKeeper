package com.bashkevich.tennisscorekeeper.components.participant

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import tennisscorekeeper.shared.generated.resources.Res
import tennisscorekeeper.shared.generated.resources.seeded_players_count



@Composable
fun SeededPlayersCountComponent(
    modifier: Modifier = Modifier,
    seedAmount: Int,
    onValueChange: (Int) -> Unit
) {
    // Допустимые количества сеянных игроков; переключение идёт по соседним элементам списка
    val seededPlayersCountOptions = listOf(0, 2, 4, 8, 16, 32)

    val minSeedAmount = seededPlayersCountOptions.first()
    val maxSeedAmount = seededPlayersCountOptions.last()
    val currentIndex = seededPlayersCountOptions.indexOf(seedAmount)
        .coerceIn(0, seededPlayersCountOptions.lastIndex)

    Row(
        modifier = Modifier.then(modifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(stringResource(Res.string.seeded_players_count))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ToggleSeededPlayersCountButton(
                modifier = Modifier.size(32.dp),
                onClick = { onValueChange(seededPlayersCountOptions[currentIndex - 1]) },
                text = "-",
                enabled = seedAmount > minSeedAmount
            )
            Text(seedAmount.toString())
            ToggleSeededPlayersCountButton(
                modifier = Modifier.size(32.dp),
                onClick = { onValueChange(seededPlayersCountOptions[currentIndex + 1]) },
                text = "+",
                enabled = seedAmount < maxSeedAmount
            )
        }
    }
}

@Composable
fun ToggleSeededPlayersCountButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    text: String,
    enabled: Boolean,
) {
    TextButton(
        modifier = Modifier.then(modifier),
        onClick = onClick,
        enabled = enabled
    ) {
        Text(text = text)
    }
}
