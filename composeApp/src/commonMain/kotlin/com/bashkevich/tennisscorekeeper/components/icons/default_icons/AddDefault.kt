package com.bashkevich.tennisscorekeeper.components.icons.default_icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup

val IconGroup.Default.Add: ImageVector
    get() {
        if (_AddDefault != null) {
            return _AddDefault!!
        }
        _AddDefault = ImageVector.Builder(
            name = "AddDefault",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFF1F1F1F))) {
                moveTo(440f, 520f)
                lineTo(200f, 520f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(240f)
                verticalLineToRelative(-240f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(240f)
                horizontalLineToRelative(240f)
                verticalLineToRelative(80f)
                lineTo(520f, 520f)
                verticalLineToRelative(240f)
                horizontalLineToRelative(-80f)
                verticalLineToRelative(-240f)
                close()
            }
        }.build()

        return _AddDefault!!
    }

@Suppress("ObjectPropertyName")
private var _AddDefault: ImageVector? = null
