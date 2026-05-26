package com.ns.wallflow.ui.screens

import android.app.Application
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ns.wallflow.model.Collection
import com.ns.wallflow.model.Wallpaper
import com.ns.wallflow.ui.components.CollectionCard
import com.ns.wallflow.ui.components.FilterBar
import com.ns.wallflow.ui.components.SelectionTopAppBar
import com.ns.wallflow.ui.components.WallpaperGrid
import com.ns.wallflow.ui.components.WallpaperImportBtn
import com.ns.wallflow.ui.icons.Delete
import com.ns.wallflow.ui.icons.LibraryAdd
import com.ns.wallflow.viewmodel.CollectionsViewModel
import com.ns.wallflow.viewmodel.CollectionsViewModelFactory
import com.ns.wallflow.viewmodel.WallpaperViewModel
import com.ns.wallflow.viewmodel.WallpaperViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onWallpaperClick: (Wallpaper) -> Unit
) {
    val timeFilters = listOf("Morning", "Afternoon", "Evening", "Night")
    val brightnessFilters = listOf("Light", "Dark")

    val context = LocalContext.current
    val application = context.applicationContext as Application
    val wallpaperViewModel: WallpaperViewModel = viewModel(
        factory = WallpaperViewModelFactory(application)
    )
    val collectionsViewModel: CollectionsViewModel = viewModel(
        factory = CollectionsViewModelFactory(application)
    )

    val selectedFilter by wallpaperViewModel.selectedFilter.collectAsState()
    val wallpapers by wallpaperViewModel.wallpapers.collectAsState()
    val collections by collectionsViewModel.collections.collectAsState()

    var isImporting by remember { mutableStateOf(false) }
    var importProgress by remember { mutableFloatStateOf(0f) }

    var selectedItemIds by rememberSaveable { mutableStateOf(setOf<Int>()) }
    val selectionMode by remember {
        derivedStateOf { selectedItemIds.isNotEmpty() }
    }

    var showCollectionSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )

    if (showCollectionSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCollectionSheet = false },
            sheetState = sheetState,
            modifier = Modifier.fillMaxHeight()
        ) {
            LazyVerticalGrid(GridCells.Adaptive(minSize = 100.dp)) {
                items(collections) { collection ->
                    CollectionCard(
                        collection,
                        onClick = {
                            showCollectionSheet = false
                            collectionsViewModel.addWallpapersToCollection(
                                selectedItemIds,
                                collection.id
                            )
                            selectedItemIds = emptySet()
                        },
                        modifier = Modifier
                            .padding(12.dp)
                            .size(130.dp)
                    )
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            AnimatedContent(
                targetState = selectionMode,
                label = "topBarTransition",
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(
                        animationSpec = tween(
                            300
                        )
                    )
                }
            ) { isSelection ->
                if (isSelection) {
                    SelectionTopAppBar(
                        isSelectionMode = true,
                        selectedCount = selectedItemIds.size,
                        onClearSelection = { selectedItemIds = emptySet() },
                        actions = {
                            IconButton(onClick = { showCollectionSheet = true }) {
                                Icon(LibraryAdd, contentDescription = "Move to Collection")
                            }
                            IconButton(onClick = {
                                wallpaperViewModel.deleteWallpapers(selectedItemIds)
                                selectedItemIds = emptySet()
                            }) {
                                Icon(Delete, contentDescription = "Delete selected")
                            }
                        }
                    )
                } else {
                    Column {
                        FilterBar(
                            timeFilters = timeFilters,
                            brightnessFilters = brightnessFilters,
                            selectedFilter = selectedFilter,
                            onFilterSelected = { filter, type ->
                                wallpaperViewModel.setFilter(
                                    filter,
                                    type
                                )
                            }
                        )
                        AnimatedVisibility(visible = isImporting) {
                            LinearProgressIndicator(
                                progress = { importProgress },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (!selectionMode) {
                WallpaperImportBtn(
                    isImporting = isImporting,
                    onImportStateChanged = { importing, current, total ->
                        isImporting = importing
                        importProgress = if (total > 0) {
                            current.toFloat() / total.toFloat()
                        } else {
                            0f
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        WallpaperGrid(
            Modifier.padding(innerPadding),
            wallpapers,
            sharedTransitionScope,
            animatedVisibilityScope,
            onWallpaperClick = { wallpaper ->
                if (selectionMode) {
                    selectedItemIds = if (selectedItemIds.contains(wallpaper.id)) {
                        selectedItemIds - wallpaper.id
                    } else {
                        selectedItemIds + wallpaper.id
                    }
                } else {
                    onWallpaperClick(wallpaper)
                }
            },
            selectedItemIds,
            { newSet -> selectedItemIds = newSet },
             selectionMode
        )
    }
}

@Preview
@Composable
fun CollectionDialogPreview() {
    val collections = listOf(
        Collection(1, "Nature", 5, ""),
        Collection(2, "Cities", 0, ""),
        Collection(3, "Abstract", 4, ""),
        Collection(4, "Animals", 2, ""),
        Collection(5, "Space", 3, ""),
        Collection(6, "Minimal", 1, ""),
        Collection(7, "Art", 6, ""),
    )
    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 150.dp)) {
        items(items = collections) { collection ->
            CollectionCard(
                collection,
                onClick = {},
                modifier = Modifier
                    .padding(12.dp)
                    .size(150.dp)
            )
        }
    }
}