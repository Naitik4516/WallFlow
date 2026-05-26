package com.ns.wallflow.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.ns.wallflow.data.AppDatabase
import com.ns.wallflow.model.Wallpaper
import com.ns.wallflow.ui.icons.Favorite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sign
import kotlin.random.Random

private class UnreactiveBoundsHolder {
    var rect: Rect = Rect.Zero
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WallpaperCard(
    wallpaper: Wallpaper,
    modifier: Modifier = Modifier,
    columnIndex: Int = 0,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onClick: (Rect) -> Unit,
    onLongClick: ((Wallpaper) -> Unit)? = null
) {
    val windowInfo = LocalWindowInfo.current
    val screenHeight = windowInfo.containerSize.height.toFloat()

    var parallaxOffset by remember { mutableFloatStateOf(0f) }
    val boundsHolder = remember { UnreactiveBoundsHolder() }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    with(sharedTransitionScope) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    // Save the rect data quietly in our plain object wrapper
                    val currentBounds = coordinates.boundsInWindow()
                    boundsHolder.rect = currentBounds

                    val position = coordinates.positionInWindow().y
                    val height = coordinates.size.height.toFloat()
                    val center = position + height / 2f
                    val ratio = (center - screenHeight / 2f) / (screenHeight / 2f)

                    val curvedRatio = sign(ratio) * abs(ratio).toDouble().pow(1.5).toFloat()
                    val columnSpeedMultiplier = if (columnIndex == 0) 1.2f else 0.9f

                    parallaxOffset = curvedRatio * (height * 0.15f) * columnSpeedMultiplier
                }
                .combinedClickable(
                    onClick = { onClick(boundsHolder.rect) },
                    onLongClick = { onLongClick?.invoke(wallpaper) }
                ),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Box {
                AsyncImage(
                    model = wallpaper.filePath,
                    contentDescription = "Wallpaper Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Random.nextInt(280, 350).dp)
                        .graphicsLayer {
                            // State read happens safely entirely inside the isolated draw layer
                            translationY = parallaxOffset
                            scaleX = 1.3f
                            scaleY = 1.3f
                        }
                        .sharedElement(
                            rememberSharedContentState(key = "image-${wallpaper.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                )
                IconButton(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            val dao = AppDatabase.getDatabase(context, scope).wallpaperDao()
                            dao.updateFavouriteStatus(wallpaper.id, !wallpaper.isFavourite)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Favorite,
                        contentDescription = "Favourite",
                        tint = if (wallpaper.isFavourite) Color.Red else Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TagChip(wallpaper.timePhase.name)
                    TagChip(wallpaper.brightness.name)
                }
            }
        }
    }
}


@Composable
fun TagChip(label: String) {
    Text(
        label,
        modifier = Modifier
            .padding(2.dp)
            .background(
                color = Color.Black.copy(alpha = 0.4f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 10.dp, vertical = 4.dp),
        style = TextStyle(
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 11.sp
        )
    )
}


//@Preview
//@Composable
//fun WallpaperCardPreview() {
//    val dummyWallpaper = Wallpaper(
//        id = 1,
//        filePath = "file:///android_asset/wallpapers/wallpaper20.webp",
//        timePhase = WallpaperTimePhase.MORNING,
//        brightness = WallpaperBrightness.LIGHT
//    )
//    SharedTransitionLayout {
//        AnimatedVisibility(visible = true) {
//            WallpaperCard(
//                wallpaper = dummyWallpaper,
//                sharedTransitionScope = this@SharedTransitionLayout,
//                animatedVisibilityScope = this@AnimatedVisibility,
//                onClick = {}
//            )
//        }
//    }
//}