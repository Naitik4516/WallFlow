package com.ns.wallflow.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

fun getFavorite(filled: Boolean = true): ImageVector {
    return ImageVector.Builder(
        name = "Favorite",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            fill = if (filled) SolidColor(Color.Magenta) else null,
            stroke = null,
            strokeLineWidth = 0.0f,
            strokeLineCap = StrokeCap.Butt,
            strokeLineJoin = StrokeJoin.Miter,
            strokeLineMiter = 4.0f,
            pathFillType = PathFillType.NonZero
        ) {
            moveTo(12.1f, 8.64f)
            lineTo(10.0f, 6.5f)
            lineTo(7.9f, 8.64f)
            curveTo(5.1f, 11.39f, 5.8f, 15.36f, 8.55f, 18.14f)
            lineTo(12.1f, 21.7f)
            lineTo(15.65f, 18.14f)
            curveTo(18.4f, 15.36f, 19.1f, 11.39f, 16.3f, 8.64f)
            close()
        }
    }.build()
}

@Suppress("CheckReturnValue")
val Favorite: ImageVector
    get() {
        if (_Favorite != null) {
            return _Favorite!!
        }
        _Favorite = getFavorite()

        return _Favorite!!
    }

private var _Favorite: ImageVector? = null

@Suppress("CheckReturnValue")
val FavoriteBorder: ImageVector
    get() {
        if (_FavoriteBorder != null) {
            return _FavoriteBorder!!
        }
        _FavoriteBorder = getFavorite(false)

        return _FavoriteBorder!!
    }

private var _FavoriteBorder: ImageVector? = null

