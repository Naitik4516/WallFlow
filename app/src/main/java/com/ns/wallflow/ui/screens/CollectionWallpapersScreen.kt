package com.ns.wallflow.ui.screens

import android.app.Application
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ns.wallflow.model.Collection
import com.ns.wallflow.model.Wallpaper
import com.ns.wallflow.ui.components.SelectionTopAppBar
import com.ns.wallflow.ui.components.WallpaperGrid
import com.ns.wallflow.ui.icons.Close
import com.ns.wallflow.ui.icons.Delete
import com.ns.wallflow.ui.icons.add
import com.ns.wallflow.ui.icons.arrow_back
import com.ns.wallflow.viewmodel.CollectionWallpapersViewModel
import com.ns.wallflow.viewmodel.CollectionWallpapersViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionWallpapersScreen(
    collection: Collection,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBack: () -> Unit,
    onWallpaperClick: (Wallpaper) -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: CollectionWallpapersViewModel = viewModel(
        factory = CollectionWallpapersViewModelFactory(application, collection.id, collection.name)
    )

    val wallpapers by viewModel.wallpapers.collectAsState()
    val otherWallpapers by viewModel.allOtherWallpapers.collectAsState()

    var selectedItemIds by rememberSaveable { mutableStateOf(setOf<Int>()) }
    val selectionMode by remember {
        derivedStateOf { selectedItemIds.isNotEmpty() }
    }

    var showWallpaperSelect by remember { mutableStateOf(false) }
    var selectedWallpapers by remember { mutableStateOf(setOf<Int>()) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)


    if (showWallpaperSelect) {
        ModalBottomSheet(
            onDismissRequest = { showWallpaperSelect = false },
            sheetState = sheetState,
        ) {
            Scaffold(
                bottomBar = {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        onClick = {
                            viewModel.addWallpapersToCollection(selectedWallpapers)
                            showWallpaperSelect = false

                        },
                        enabled = selectedWallpapers.isNotEmpty()
                    ) {
                        Text("Add to Collection")
                    }
                }
            ) { innerPadding ->
                WallpaperGrid(
                    Modifier.padding(innerPadding),
                    wallpapers = otherWallpapers,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onWallpaperClick = { wallpaper ->
                        selectedWallpapers = if (selectedWallpapers.contains(wallpaper.id)) {
                            selectedWallpapers - wallpaper.id
                        } else {
                            selectedWallpapers + wallpaper.id
                        }
                    },
                    selectedItemIds = selectedWallpapers,
                    updateSelectedItemIds = { newSet -> selectedWallpapers = newSet },
                    selectionMode = true
                )
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                if (selectionMode) {
                    SelectionTopAppBar(
                        isSelectionMode = selectionMode,
                        selectedCount = selectedItemIds.size,
                        onClearSelection = { selectedItemIds = emptySet() },
                        actions = {
                            IconButton(onClick = {
                                viewModel.removeWallpapersFromCollection(
                                    selectedItemIds,
                                    collection.id
                                )
                                selectedItemIds = emptySet()
                            }) {
                                Icon(Close, contentDescription = "Remove from collection")
                            }
                            IconButton(onClick = {
                                viewModel.deleteWallpapers(selectedItemIds)
                                selectedItemIds = emptySet()
                            }) {
                                Icon(Delete, contentDescription = "Delete")
                            }
                        }
                    )
                } else {
                    TopAppBar(
                        title = { Text(collection.name) },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(arrow_back, contentDescription = "Back")
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            if (!selectionMode) {
                FloatingActionButton(onClick = {
                    selectedWallpapers = emptySet()
                    showWallpaperSelect = true
                }) {
                    Icon(add, contentDescription = "Add Wallpapers")
                }
            }
        }
    ) { innerPadding ->
        WallpaperGrid(
            Modifier.padding(innerPadding),
            wallpapers,
            sharedTransitionScope,
            animatedVisibilityScope,
            onWallpaperClick,
            selectedItemIds,
            { newSet -> selectedItemIds = newSet },
            selectionMode
        )
    }
}
