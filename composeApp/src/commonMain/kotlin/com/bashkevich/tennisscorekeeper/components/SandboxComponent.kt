package com.bashkevich.tennisscorekeeper.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun SandboxComponent() {
    var totalHeight by remember { mutableStateOf(0) }

    val density = LocalDensity.current
    val totalHeightDp = with(density) { totalHeight.toDp() }
    Row(
        modifier = Modifier.background(Color.Blue)
    ) {
        Column(modifier = Modifier.width(32.dp).height(totalHeightDp).background(Color.Yellow)) {
            Box(modifier = Modifier.fillMaxWidth().weight(1f).background(Color.Red))
            Box(modifier = Modifier.fillMaxWidth().weight(1f).background(Color.Green))
        }
        Column(
            modifier = Modifier
                .onGloballyPositioned { layoutCoordinates ->
                    totalHeight = layoutCoordinates.size.height
                }
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

//@Composable
//fun AlignByBaselineSample() {
//    Row(modifier = Modifier.fillMaxHeight()) {
//        // The center of the magenta Box and the baselines of the two texts will be
//        // vertically aligned. Note that alignBy() or alignByBaseline() has to be specified
//        // for all children we want to take part in the alignment. For example, alignByBaseline()
//// means that the baseline of the text should be aligned with the alignment line
//        // (possibly another baseline) specified for siblings using alignBy or alignByBaseline.
//        // If no other sibling had alignBy() or alignByBaseline(), the modifier would have no
//        // effect.
//        Box(modifier = Modifier.size(80.dp, 40.dp).alignBy { it.measuredHeight / 2 }
//            .background(Color.Magenta)) {
//            Text(
//                text =
//                    "Text 1",
//                fontSize = 40.sp,
//                modifier = Modifier.background(color = Color.Red).alignByBaseline()
//            )
//            Text(
//                text = "Text 2",
//                modifier = Modifier.alignByBaseline().background(color = Color.Cyan)
//            )
//        }
//    }
//}