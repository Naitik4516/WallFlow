package com.ns.wallflow.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.material3.Scaffold
import com.ns.wallflow.ui.components.FilterBar
import com.ns.wallflow.ui.components.WallpaperImportBtn
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ns.wallflow.viewmodel.HomeViewModel
import com.ns.wallflow.viewmodel.HomeViewModelFactory
import androidx.compose.ui.platform.LocalContext
import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.ns.wallflow.model.Wallpaper
import com.ns.wallflow.ui.components.SelectionTopAppBar
import com.ns.wallflow.ui.components.WallpaperGrid
import com.ns.wallflow.ui.icons.Delete
import com.ns.wallflow.ui.icons.LibraryAdd
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
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(application)
    )

    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val wallpapers by viewModel.wallpapers.collectAsState()

    var isImporting by remember { mutableStateOf(false) }
    var importProgress by remember { mutableFloatStateOf(0f) }

    var selectedItemIds by rememberSaveable { mutableStateOf(setOf<Int>()) }
    val selectionMode by remember {
        derivedStateOf { selectedItemIds.isNotEmpty() }
    }


    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (selectionMode) {
                SelectionTopAppBar(
                    isSelectionMode = selectionMode,
                    selectedCount = selectedItemIds.size,
                    onClearSelection = { selectedItemIds = emptySet() },
                    actions = {
                        if (selectionMode) {
                            IconButton(onClick = {}) {
                                Icon(LibraryAdd, contentDescription = "Move to Collection")
                            }
                            IconButton(onClick = { }) {
                                Icon(Delete, contentDescription = "Delete selected")
                            }
                        }
                    }
                )
            } else {
                Column {
                    FilterBar(
                        timeFilters = timeFilters,
                        brightnessFilters = brightnessFilters,
                        selectedFilter = selectedFilter,
                        onFilterSelected = { filter, type -> viewModel.setFilter(filter, type) }
                    )
                    AnimatedVisibility(visible = isImporting) {
                        LinearProgressIndicator(
                            progress = { importProgress },
                            modifier = Modifier.fillMaxWidth()
                        )
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
            wallpapers.reversed(),
            sharedTransitionScope,
            animatedVisibilityScope,
            onWallpaperClick,
            selectedItemIds,
            { newSet -> selectedItemIds = newSet },
             selectionMode
        )
    }
}
