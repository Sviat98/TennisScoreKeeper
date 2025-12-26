package com.bashkevich.tennisscorekeeper.components.add_match.participant

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Add
import com.bashkevich.tennisscorekeeper.components.icons.default_icons.Close
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipant
import com.bashkevich.tennisscorekeeper.model.participant.domain.TennisParticipantInMatch

@Composable
fun AddMatchParticipantComponent(
    modifier: Modifier = Modifier,
    participantOptions: List<TennisParticipant>,
    participant: TennisParticipantInMatch,
    onParticipantsFetch: () -> Unit,
    onParticipantChange: (TennisParticipant) -> Unit,
    onParticipantDisplayNameChange: (String) -> Unit,
    participantPrimaryColor: Color,
    participantSecondaryColor: Color?,
    onColorPickerOpen: (Int) -> Unit,
    onToggleSecondaryColor: (Color?) -> Unit
) {
    Column(
        modifier = Modifier.then(modifier),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ParticipantCombobox(
            modifier = Modifier.widthIn(max = 300.dp).fillMaxWidth(),
            participantOptions = participantOptions,
            currentParticipant = participant,
            onParticipantsFetch = onParticipantsFetch,
            onParticipantChange = onParticipantChange
        )

        ParticipantDisplayNameComponent(
            modifier = Modifier.widthIn(max = 300.dp).fillMaxWidth(),
            participant = participant,
            onParticipantDisplayNameChange = onParticipantDisplayNameChange
        )

        ParticipantColorPickerBlock(
            primaryColor = participantPrimaryColor,
            secondaryColor = participantSecondaryColor,
            onColorPickerOpen = onColorPickerOpen,
            onToggleSecondaryColor = onToggleSecondaryColor,
        )
    }
}

@Composable
fun ParticipantColorPickerBlock(
    primaryColor: Color,
    secondaryColor: Color?,
    onColorPickerOpen: (Int) -> Unit,
    onToggleSecondaryColor: (Color?) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Colors")
        PrimaryColorPicker(color = primaryColor, onColorPickerOpen = onColorPickerOpen)
        SecondaryColorPicker(
            color = secondaryColor,
            onColorPickerOpen = onColorPickerOpen,
            onToggleSecondaryColor = onToggleSecondaryColor
        )
    }
}

@Composable
fun PrimaryColorPicker(color: Color, onColorPickerOpen: (Int) -> Unit) {
    ColorPickerButton(
        modifier = Modifier.size(32.dp),
        color = color,
        colorNumber = 1,
        onColorPickerOpen = onColorPickerOpen
    )
}

@Composable
fun SecondaryColorPicker(
    color: Color?, onColorPickerOpen: (Int) -> Unit,
    onToggleSecondaryColor: (Color?) -> Unit
) {

    if (color != null) {
        Box {
            ColorPickerButton(
                modifier = Modifier.size(32.dp),
                color = color,
                colorNumber = 2,
                onColorPickerOpen = onColorPickerOpen
            )
            // Кнопка с крестиком в правом верхнем углу
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 4.dp, y = (-4).dp)
            ) {
                Icon(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color.Black)
                        .clickable {
                            onToggleSecondaryColor(null)
                        },
                    imageVector = IconGroup.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }

        }
    } else {
        AddSecondaryColorButton(
            modifier = Modifier.size(32.dp),
            onAddSecondaryColor = onToggleSecondaryColor
        )
    }

}

@Composable
fun AddSecondaryColorButton(
    modifier: Modifier = Modifier,
    onAddSecondaryColor: (Color) -> Unit
) {
    Box(
        Modifier.then(modifier)
            .background(color = Color.White, shape = RoundedCornerShape(4.dp))
            .border(
                border = BorderStroke(width = 1.dp, color = Color.Black),
                shape = RoundedCornerShape(4.dp)
            )
    ) {
        Icon(
            modifier = Modifier.align(Alignment.Center).clickable {
                onAddSecondaryColor(Color.White)
            },
            imageVector = IconGroup.Default.Add,
            contentDescription = "Add secondary color",
            tint = Color.Black
        )
    }
}

@Composable
fun ColorPickerButton(
    modifier: Modifier = Modifier,
    color: Color,
    colorNumber: Int,
    onColorPickerOpen: (Int) -> Unit
) {
    Button(
        onClick = { onColorPickerOpen(colorNumber) },
        modifier = Modifier.then(modifier), // Фиксированный размер для выравнивания
        border = BorderStroke(width = 1.dp, color = Color.Black),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
        )
    ) {
        // Пустое содержимое кнопки
    }
}