package com.ns.wallflow.ui.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ns.wallflow.model.Wallpaper

@Composable
fun WallpaperGrid(
    modifier: Modifier = Modifier,
    wallpapers: List<Wallpaper>,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    onWallpaperClick: (Wallpaper) -> Unit,
    selectedItemIds: Set<Int> = emptySet(),
    updateSelectedItemIds: (Set<Int>) -> Unit = {},
    selectionMode: Boolean = false,
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalItemSpacing = 12.dp
    ) {
        itemsIndexed(
            items = wallpapers,
            key = { _, wallpaper -> wallpaper.id }) { index, wallpaper ->
            val isSelected = selectedItemIds.contains(wallpaper.id)
            SelectableGridItem(
                isSelected = isSelected,
                isSelectionMode = selectionMode,
                modifier = Modifier.animateItem(),
                onCheckedChange = {
                    if (selectionMode) {
                        updateSelectedItemIds(
                            if (isSelected) {
                                selectedItemIds - wallpaper.id
                            } else {
                                selectedItemIds + wallpaper.id
                            }
                        )
                    }
                }
            ) {
                WallpaperCard(
                    wallpaper = wallpaper,
                    columnIndex = index % 2,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onClick = { onWallpaperClick(wallpaper) },
                    onLongClick = {
                        if (!selectionMode) {
                            updateSelectedItemIds(selectedItemIds + wallpaper.id)
                        }
                    }
                )
            }
        }
    }
}
