package com.bashkevich.tennisscorekeeper.components.icons.default_icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup

val IconGroup.Default.Preview: ImageVector
    get() {
        if (_Preview != null) {
            return _Preview!!
        }
        _Preview = ImageVector.Builder(
            name = "Preview",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(fill = SolidColor(Color(0xFF1F1F1F))) {
                moveTo(200f, 840f)
                quadToRelative(-33f, 0f, -56.5f, -23.5f)
                reflectiveQuadTo(120f, 760f)
                verticalLineToRelative(-560f)
                quadToRelative(0f, -33f, 23.5f, -56.5f)
                reflectiveQuadTo(200f, 120f)
                horizontalLineToRelative(560f)
                quadToRelative(33f, 0f, 56.5f, 23.5f)
                reflectiveQuadTo(840f, 200f)
                verticalLineToRelative(560f)
                quadToRelative(0f, 33f, -23.5f, 56.5f)
                reflectiveQuadTo(760f, 840f)
                lineTo(200f, 840f)
                close()
                moveTo(200f, 760f)
                horizontalLineToRelative(560f)
                verticalLineToRelative(-480f)
                lineTo(200f, 280f)
                verticalLineToRelative(480f)
                close()
                moveTo(333.5f, 635.5f)
                quadTo(269f, 591f, 240f, 520f)
                quadToRelative(29f, -71f, 93.5f, -115.5f)
                reflectiveQuadTo(480f, 360f)
                quadToRelative(82f, 0f, 146.5f, 44.5f)
                reflectiveQuadTo(720f, 520f)
                quadToRelative(-29f, 71f, -93.5f, 115.5f)
                reflectiveQuadTo(480f, 680f)
                quadToRelative(-82f, 0f, -146.5f, -44.5f)
                close()
                moveTo(582f, 593.5f)
                quadToRelative(46f, -26.5f, 72f, -73.5f)
                quadToRelative(-26f, -47f, -72f, -73.5f)
                reflectiveQuadTo(480f, 420f)
                quadToRelative(-56f, 0f, -102f, 26.5f)
                reflectiveQuadTo(306f, 520f)
                quadToRelative(26f, 47f, 72f, 73.5f)
                reflectiveQuadTo(480f, 620f)
                quadToRelative(56f, 0f, 102f, -26.5f)
                close()
                moveTo(480f, 520f)
                close()
                moveTo(522.5f, 562.5f)
                quadTo(540f, 545f, 540f, 520f)
                reflectiveQuadToRelative(-17.5f, -42.5f)
                quadTo(505f, 460f, 480f, 460f)
                reflectiveQuadToRelative(-42.5f, 17.5f)
                quadTo(420f, 495f, 420f, 520f)
                reflectiveQuadToRelative(17.5f, 42.5f)
                quadTo(455f, 580f, 480f, 580f)
                reflectiveQuadToRelative(42.5f, -17.5f)
                close()
            }
        }.build()

        return _Preview!!
    }

@Suppress("ObjectPropertyName")
private var _Preview: ImageVector? = null
