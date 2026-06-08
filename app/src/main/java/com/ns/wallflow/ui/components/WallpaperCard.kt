package com.ns.wallflow.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.ns.wallflow.ui.icons.favorite
import com.ns.wallflow.ui.icons.favorite_filled
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sign

private class UnreactiveBoundsHolder {
    var rect: Rect = Rect.Zero
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WallpaperCard(
    wallpaper: Wallpaper,
    modifier: Modifier = Modifier,
    columnIndex: Int = 0,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    onClick: (Rect) -> Unit,
    onLongClick: ((Wallpaper) -> Unit)? = null
) {
    val windowInfo = LocalWindowInfo.current
    val screenHeight = windowInfo.containerSize.height.toFloat()

    val cardHeight = rememberSaveable(wallpaper.id) { 250 + (abs(wallpaper.id * 31) % 71) }

    var parallaxOffset by remember { mutableFloatStateOf(0f) }
    val boundsHolder = remember { UnreactiveBoundsHolder() }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cardInteractionSource = remember { MutableInteractionSource() }

    val cardContent = @Composable {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .then(
                    if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                        with(sharedTransitionScope) {
                            with(animatedVisibilityScope) {
                                Modifier.animateEnterExit(
                                    enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { it / 2 },
                                    exit = fadeOut(tween(600)) + slideOutVertically(tween(600)) { -it / 2 }
                                )
                            }
                        }
                    } else Modifier
                )
                .onGloballyPositioned { coordinates ->
                    val currentBounds = coordinates.boundsInWindow()
                    boundsHolder.rect = currentBounds

                    val position = coordinates.positionInWindow().y
                    val height = coordinates.size.height.toFloat()
                    val center = position + height / 2f
                    val ratio = (center - screenHeight / 2f) / (screenHeight / 2f)

                    val curvedRatio = sign(ratio) * abs(ratio).toDouble().pow(1.5).toFloat()

                    // Determine actual column from X position for more robust parallax
                    val x = coordinates.positionInWindow().x
                    val actualColumn = if (x < windowInfo.containerSize.width / 2f) 0 else 1
                    val columnSpeedMultiplier = if (actualColumn == 0) 1.2f else 0.9f

                    parallaxOffset = curvedRatio * (height * 0.15f) * columnSpeedMultiplier
                }
                .combinedClickable(
                    interactionSource = cardInteractionSource,
                    indication = ripple(),
                    onClick = { onClick(boundsHolder.rect) },
                    onLongClick = { onLongClick?.invoke(wallpaper) }
                ),
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Box {
                var isLiked by remember(wallpaper.isFavourite) { mutableStateOf(wallpaper.isFavourite) }

                AsyncImage(
                    model = wallpaper.filePath,
                    contentDescription = "Wallpaper Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(cardHeight.dp)
                        .graphicsLayer {
                            translationY = parallaxOffset
                            scaleX = 1.3f
                            scaleY = 1.3f
                        }
                        .then(
                            if (sharedTransitionScope != null && animatedVisibilityScope != null) {
                                with(sharedTransitionScope) {
                                    Modifier.sharedElement(
                                        rememberSharedContentState(key = "image-${wallpaper.id}"),
                                        animatedVisibilityScope = animatedVisibilityScope
                                    )
                                }
                            } else Modifier
                        )
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                        .size(32.dp)
                        .background(Color.Black.copy(alpha = 0.2f), CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = false, color = Color.Red),
                            onClick = {
                                val newLikedState = !isLiked
                                isLiked = newLikedState
                                scope.launch(Dispatchers.IO) {
                                    val dao = AppDatabase.getDatabase(context).wallpaperDao()
                                    dao.updateFavouriteStatus(wallpaper.id, newLikedState)
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isLiked) favorite_filled else favorite,
                        contentDescription = "Favourite",
                        tint = if (isLiked) Color.Red else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(4.dp),
                ) {
                    wallpaper.tags.take(3).forEach { tag ->
                        TagChip(tag)
                    }
                }
            }
        }
    }

    if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            with(animatedVisibilityScope) {
                cardContent()
            }
        }
    } else {
        cardContent()
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
