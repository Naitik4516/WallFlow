package com.ns.wallflow.ui.screens

import android.app.Application
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ns.wallflow.model.Collection
import com.ns.wallflow.model.Wallpaper
import com.ns.wallflow.ui.components.WallpaperGrid
import com.ns.wallflow.ui.components.WallpaperImportBtn
import com.ns.wallflow.viewmodel.CollectionWallpapersViewModel
import com.ns.wallflow.viewmodel.CollectionWallpapersViewModelFactory
import com.ns.wallflow.ui.icons.arrow_back
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxWidth
import com.ns.wallflow.ui.icons.Close
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.ns.wallflow.ui.components.SelectionTopAppBar

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

    var selectedItemIds by rememberSaveable { mutableStateOf(setOf<Int>()) }
    val selectionMode by remember {
        derivedStateOf { selectedItemIds.isNotEmpty() }
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
                                viewModel.removeWallpapersFromCollection(selectedItemIds, collection.id)
                                selectedItemIds = emptySet()
                            }) {
                                Icon(Close, contentDescription = "Remove from collection")
                            }
                            IconButton(onClick = {
                                viewModel.deleteWallpapers(selectedItemIds)
                                selectedItemIds = emptySet()
                            }) {
                                Icon(Close, contentDescription = "Delete")
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
//        floatingActionButton = {
//        }
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
