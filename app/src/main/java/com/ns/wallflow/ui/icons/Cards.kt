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
public val cards: ImageVector
    get() {
        if (_cards != null) {
            return _cards!!
        }
        _cards =
            ImageVector.Builder(
                name = "cards",
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
                        moveTo(6f, 11.5f)
                        verticalLineTo(6f)
                        horizontalLineToRelative(5.5f)
                        verticalLineToRelative(5.5f)
                        horizontalLineTo(6f)
                        close()
                        moveTo(6f, 18f)
                        verticalLineTo(12.5f)
                        horizontalLineToRelative(5.5f)
                        verticalLineTo(18f)
                        horizontalLineTo(6f)
                        close()
                        moveToRelative(6.5f, -6.5f)
                        verticalLineTo(6f)
                        horizontalLineTo(18f)
                        verticalLineToRelative(5.5f)
                        horizontalLineTo(12.5f)
                        close()
                        moveToRelative(0f, 6.5f)
                        verticalLineTo(12.5f)
                        horizontalLineTo(18f)
                        verticalLineTo(18f)
                        horizontalLineTo(12.5f)
                        close()
                        moveTo(8f, 9.5f)
                        horizontalLineTo(9.5f)
                        verticalLineTo(8f)
                        horizontalLineTo(8f)
                        verticalLineTo(9.5f)
                        close()
                        moveToRelative(6.5f, 0f)
                        horizontalLineTo(16f)
                        verticalLineTo(8f)
                        horizontalLineTo(14.5f)
                        verticalLineTo(9.5f)
                        close()
                        moveTo(8f, 16f)
                        horizontalLineTo(9.5f)
                        verticalLineTo(14.5f)
                        horizontalLineTo(8f)
                        verticalLineTo(16f)
                        close()
                        moveToRelative(6.5f, 0f)
                        horizontalLineTo(16f)
                        verticalLineTo(14.5f)
                        horizontalLineTo(14.5f)
                        verticalLineTo(16f)
                        close()
                        moveTo(9.5f, 9.5f)
                        close()
                        moveToRelative(5f, 0f)
                        close()
                        moveToRelative(0f, 5f)
                        close()
                        moveToRelative(-5f, 0f)
                        close()
                        moveTo(5f, 21f)
                        quadTo(4.18f, 21f, 3.59f, 20.41f)
                        reflectiveQuadTo(3f, 19f)
                        verticalLineTo(5f)
                        quadTo(3f, 4.17f, 3.59f, 3.59f)
                        reflectiveQuadTo(5f, 3f)
                        horizontalLineTo(19f)
                        quadToRelative(0.83f, 0f, 1.41f, 0.59f)
                        reflectiveQuadTo(21f, 5f)
                        verticalLineTo(19f)
                        quadToRelative(0f, 0.82f, -0.59f, 1.41f)
                        reflectiveQuadTo(19f, 21f)
                        horizontalLineTo(5f)
                        close()
                        moveTo(5f, 19f)
                        horizontalLineTo(19f)
                        verticalLineTo(5f)
                        horizontalLineTo(5f)
                        verticalLineTo(19f)
                        close()
                    }
                }
                .build()
        return _cards!!
    }

private var _cards: ImageVector? = null