package com.bashkevich.tennisscorekeeper.components.icons.default_icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup

val IconGroup.Default.ArrowForward: ImageVector
    get() {
        if (_ArrowForwardDefault != null) {
            return _ArrowForwardDefault!!
        }
        _ArrowForwardDefault = ImageVector.Builder(
            name = "ArrowForwardDefault",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFF1F1F1F))) {
                moveTo(647f, 520f)
                lineTo(160f, 520f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(487f)
                lineTo(423f, 216f)
                lineToRelative(57f, -56f)
                lineToRelative(320f, 320f)
                lineToRelative(-320f, 320f)
                lineToRelative(-57f, -56f)
                lineToRelative(224f, -224f)
                close()
            }
        }.build()

        return _ArrowForwardDefault!!
    }

@Suppress("ObjectPropertyName")
private var _ArrowForwardDefault: ImageVector? = null
