package com.bashkevich.tennisscorekeeper.components.icons.default_icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.bashkevich.tennisscorekeeper.components.icons.IconGroup

val IconGroup.Default.Group: ImageVector
    get() {
        if (_Group != null) {
            return _Group!!
        }
        _Group = ImageVector.Builder(
            name = "Group",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color(0xFF1F1F1F))) {
                moveTo(1f, 20f)
                lineTo(1f, 17.2f)
                quadToRelative(0f, -0.85f, 0.44f, -1.56f)
                quadToRelative(0.44f, -0.71f, 1.16f, -1.09f)
                quadToRelative(1.55f, -0.78f, 3.15f, -1.16f)
                reflectiveQuadTo(9f, 13f)
                reflectiveQuadToRelative(3.25f, 0.39f)
                reflectiveQuadToRelative(3.15f, 1.16f)
                quadToRelative(0.72f, 0.38f, 1.16f, 1.09f)
                reflectiveQuadTo(17f, 17.2f)
                lineTo(17f, 20f)
                lineTo(1f, 20f)
                close()
                moveTo(19f, 20f)
                lineTo(19f, 17f)
                quadToRelative(0f, -1.1f, -0.61f, -2.11f)
                quadToRelative(-0.61f, -1.01f, -1.74f, -1.74f)
                quadToRelative(1.27f, 0.15f, 2.4f, 0.51f)
                quadToRelative(1.13f, 0.36f, 2.1f, 0.89f)
                quadToRelative(0.9f, 0.5f, 1.38f, 1.11f)
                reflectiveQuadTo(23f, 17f)
                verticalLineToRelative(3f)
                lineTo(19f, 20f)
                close()
                moveTo(6.18f, 10.83f)
                quadTo(5f, 9.65f, 5f, 8f)
                reflectiveQuadTo(6.18f, 5.18f)
                reflectiveQuadTo(9f, 4f)
                reflectiveQuadToRelative(2.83f, 1.18f)
                reflectiveQuadTo(13f, 8f)
                reflectiveQuadToRelative(-1.17f, 2.82f)
                reflectiveQuadTo(9f, 12f)
                reflectiveQuadTo(6.18f, 10.83f)
                close()
                moveTo(17.83f, 10.83f)
                quadTo(16.65f, 12f, 15f, 12f)
                quadToRelative(-0.27f, 0f, -0.7f, -0.06f)
                reflectiveQuadTo(13.6f, 11.8f)
                quadToRelative(0.67f, -0.8f, 1.04f, -1.78f)
                reflectiveQuadTo(15f, 8f)
                reflectiveQuadTo(14.64f, 5.97f)
                reflectiveQuadTo(13.6f, 4.2f)
                quadToRelative(0.35f, -0.13f, 0.7f, -0.16f)
                reflectiveQuadTo(15f, 4f)
                quadToRelative(1.65f, 0f, 2.82f, 1.18f)
                reflectiveQuadTo(19f, 8f)
                reflectiveQuadToRelative(-1.18f, 2.82f)
                close()
                moveTo(3f, 18f)
                lineTo(15f, 18f)
                lineTo(15f, 17.2f)
                quadToRelative(0f, -0.27f, -0.14f, -0.5f)
                quadToRelative(-0.14f, -0.22f, -0.36f, -0.35f)
                quadToRelative(-1.35f, -0.68f, -2.72f, -1.01f)
                reflectiveQuadTo(9f, 15f)
                reflectiveQuadTo(6.23f, 15.34f)
                reflectiveQuadTo(3.5f, 16.35f)
                quadTo(3.28f, 16.48f, 3.14f, 16.7f)
                quadTo(3f, 16.93f, 3f, 17.2f)
                lineTo(3f, 18f)
                close()
                moveTo(10.41f, 9.41f)
                quadTo(11f, 8.82f, 11f, 8f)
                reflectiveQuadTo(10.41f, 6.59f)
                reflectiveQuadTo(9f, 6f)
                quadTo(8.18f, 6f, 7.59f, 6.59f)
                quadTo(7f, 7.18f, 7f, 8f)
                reflectiveQuadTo(7.59f, 9.41f)
                reflectiveQuadTo(9f, 10f)
                quadToRelative(0.83f, 0f, 1.41f, -0.59f)
                close()
                moveTo(9f, 18f)
                close()
                moveTo(9f, 8f)
                close()
            }
        }.build()

        return _Group!!
    }

@Suppress("ObjectPropertyName")
private var _Group: ImageVector? = null
