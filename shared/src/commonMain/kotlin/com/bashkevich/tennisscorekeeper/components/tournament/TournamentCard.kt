package com.bashkevich.tennisscorekeeper.components.tournament

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Group
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Person
import com.bashkevich.tennisscorekeeper.components.status.StatusText
import com.bashkevich.tennisscorekeeper.model.tournament.domain.Tournament
import com.bashkevich.tennisscorekeeper.model.tournament.remote.TournamentType
import com.bashkevich.tennisscorekeeper.model.tournament.remote.toResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun TournamentListItem(
    modifier: Modifier = Modifier,
    tournament: Tournament,
    onTournamentClick: (Tournament) -> Unit
) {
    Card(
        onClick = { onTournamentClick(tournament) },
        modifier = Modifier.then(modifier),
        border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatusText(modifier = Modifier, status = tournament.status)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(tournament.name)
                Icon(
                    imageVector = if (tournament.type == TournamentType.SINGLES) {
                        IconGroup.Default.Person
                    } else {
                        IconGroup.Default.Group
                    },
                    contentDescription = stringResource(tournament.type.toResource())
                )
            }
        }
    }
}
