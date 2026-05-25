package com.ns.wallflow.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Suppress("CheckReturnValue")
public val Stacks: ImageVector
    get() {
        if (_stacks != null) {
            return _stacks!!
        }
        _stacks =
            ImageVector.Builder(
                name = "stacks",
                defaultWidth = 24.dp,
                defaultHeight = 24.dp,
                viewportWidth = 24f,
                viewportHeight = 24f,
            )
                .apply {
                    path(
                        fill = SolidColor(Color.Black),
                        fillAlpha = 1f,
                        stroke = null,
                        strokeAlpha = 1f,
                        strokeLineWidth = 1f,
                        strokeLineCap = StrokeCap.Butt,
                        strokeLineJoin = StrokeJoin.Bevel,
                        strokeLineMiter = 1f,
                        pathFillType = PathFillType.Companion.NonZero,
                    ) {
                        moveTo(11.51f, 13.66f)
                        quadTo(11.28f, 13.6f, 11.05f, 13.48f)
                        lineTo(2.6f, 8.88f)
                        quadTo(2.33f, 8.73f, 2.21f, 8.5f)
                        quadTo(2.1f, 8.27f, 2.1f, 8f)
                        quadTo(2.1f, 7.72f, 2.21f, 7.5f)
                        quadTo(2.33f, 7.27f, 2.6f, 7.13f)
                        lineToRelative(8.45f, -4.6f)
                        quadTo(11.28f, 2.4f, 11.51f, 2.34f)
                        reflectiveQuadTo(12f, 2.27f)
                        reflectiveQuadToRelative(0.49f, 0.06f)
                        reflectiveQuadToRelative(0.46f, 0.19f)
                        lineToRelative(8.45f, 4.6f)
                        quadToRelative(0.27f, 0.15f, 0.39f, 0.38f)
                        quadTo(21.9f, 7.72f, 21.9f, 8f)
                        quadToRelative(0f, 0.27f, -0.11f, 0.5f)
                        reflectiveQuadTo(21.4f, 8.88f)
                        lineToRelative(-8.45f, 4.6f)
                        quadToRelative(-0.22f, 0.13f, -0.46f, 0.19f)
                        reflectiveQuadTo(12f, 13.73f)
                        reflectiveQuadTo(11.51f, 13.66f)
                        close()
                        moveTo(12f, 15.73f)
                        lineToRelative(7.85f, -4.28f)
                        quadToRelative(0.05f, -0.03f, 0.48f, -0.13f)
                        quadToRelative(0.42f, 0f, 0.71f, 0.29f)
                        reflectiveQuadToRelative(0.29f, 0.71f)
                        quadToRelative(0f, 0.28f, -0.13f, 0.5f)
                        reflectiveQuadTo(20.8f, 13.2f)
                        lineToRelative(-7.85f, 4.28f)
                        quadToRelative(-0.22f, 0.13f, -0.46f, 0.19f)
                        reflectiveQuadTo(12f, 17.73f)
                        reflectiveQuadTo(11.51f, 17.66f)
                        reflectiveQuadTo(11.05f, 17.48f)
                        lineTo(3.2f, 13.2f)
                        quadTo(2.93f, 13.05f, 2.8f, 12.83f)
                        reflectiveQuadTo(2.68f, 12.33f)
                        quadToRelative(0f, -0.43f, 0.29f, -0.71f)
                        reflectiveQuadTo(3.68f, 11.33f)
                        quadToRelative(0.13f, 0f, 0.24f, 0.04f)
                        reflectiveQuadToRelative(0.24f, 0.09f)
                        lineTo(12f, 15.73f)
                        close()
                        moveToRelative(0f, 4f)
                        lineToRelative(7.85f, -4.28f)
                        quadToRelative(0.05f, -0.03f, 0.48f, -0.13f)
                        quadToRelative(0.42f, 0f, 0.71f, 0.29f)
                        reflectiveQuadToRelative(0.29f, 0.71f)
                        quadToRelative(0f, 0.28f, -0.13f, 0.5f)
                        reflectiveQuadTo(20.8f, 17.2f)
                        lineToRelative(-7.85f, 4.28f)
                        quadToRelative(-0.22f, 0.13f, -0.46f, 0.19f)
                        reflectiveQuadTo(12f, 21.73f)
                        reflectiveQuadTo(11.51f, 21.66f)
                        reflectiveQuadTo(11.05f, 21.48f)
                        lineTo(3.2f, 17.2f)
                        quadTo(2.93f, 17.05f, 2.8f, 16.83f)
                        reflectiveQuadTo(2.68f, 16.33f)
                        quadToRelative(0f, -0.42f, 0.29f, -0.71f)
                        quadTo(3.25f, 15.33f, 3.68f, 15.33f)
                        quadToRelative(0.13f, 0f, 0.24f, 0.04f)
                        reflectiveQuadToRelative(0.24f, 0.09f)
                        lineTo(12f, 19.73f)
                        close()
                    }
                }
                .build()
        return _stacks!!
    }

private var _stacks: ImageVector? = null
