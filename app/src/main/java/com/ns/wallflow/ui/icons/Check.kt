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
val Check: ImageVector
    get() {
        if (_check != null) {
            return _check!!
        }
        _check =
            ImageVector.Builder(
                name = "Check",
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
                        pathFillType = PathFillType.NonZero,
                    ) {
                        moveTo(9.55f, 18f)
                        quadToRelative(-0.42f, 0f, -0.7f, -0.28f)
                        lineToRelative(-4.42f, -4.42f)
                        quadToRelative(-0.28f, -0.28f, -0.28f, -0.7f)
                        reflectiveQuadToRelative(0.28f, -0.7f)
                        quadToRelative(0.28f, -0.28f, 0.7f, -0.28f)
                        reflectiveQuadToRelative(0.7f, 0.28f)
                        lineTo(9.55f, 15.6f)
                        lineToRelative(7.9f, -7.9f)
                        quadToRelative(0.28f, -0.28f, 0.7f, -0.28f)
                        reflectiveQuadToRelative(0.7f, 0.28f)
                        quadToRelative(0.28f, -0.28f, 0.28f, 0.7f)
                        reflectiveQuadToRelative(-0.28f, 0.7f)
                        lineToRelative(-8.6f, 8.6f)
                        quadToRelative(-0.28f, 0.28f, -0.7f, 0.28f)
                        close()
                    }
                }
                .build()
        return _check!!
    }

private var _check: ImageVector? = null
