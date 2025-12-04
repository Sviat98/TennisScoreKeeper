package com.bashkevich.tennisscorekeeper.components.icons.default_icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup

val IconGroup.Default.ArrowDropDown: ImageVector
    get() {
        if (_ArrowDropDownDefault != null) {
            return _ArrowDropDownDefault!!
        }
        _ArrowDropDownDefault = ImageVector.Builder(
            name = "ArrowDropDownDefault",
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

        return _ArrowDropDownDefault!!
    }

@Suppress("ObjectPropertyName")
private var _ArrowDropDownDefault: ImageVector? = null