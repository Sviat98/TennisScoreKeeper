package com.bashkevich.tennisscorekeeper.components.icons.filled_icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup

val IconGroup.Filled.ArrowDropDown: ImageVector
    get() {
        if (_ArrowDropDownFilled != null) {
            return _ArrowDropDownFilled!!
        }
        _ArrowDropDownFilled = ImageVector.Builder(
            name = "ArrowDropDownFilled",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFF1F1F1F))) {
                moveTo(480f, 600f)
                lineTo(280f, 400f)
                horizontalLineToRelative(400f)
                lineTo(480f, 600f)
                close()
            }
        }.build()

        return _ArrowDropDownFilled!!
    }

@Suppress("ObjectPropertyName")
private var _ArrowDropDownFilled: ImageVector? = null
