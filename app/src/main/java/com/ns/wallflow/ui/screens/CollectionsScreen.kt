package com.ns.wallflow.ui.screens

import android.app.Application
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ns.wallflow.model.Collection
import com.ns.wallflow.ui.components.CollectionCard
import com.ns.wallflow.ui.components.SelectionTopAppBar
import com.ns.wallflow.ui.icons.Delete
import com.ns.wallflow.ui.icons.Edit
import com.ns.wallflow.ui.icons.add
import com.ns.wallflow.viewmodel.CollectionsViewModel
import com.ns.wallflow.viewmodel.CollectionsViewModelFactory

@Preview
@Composable
fun CollectionsScreenPreview() {
    CollectionsScreen(onCollectionClick = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsScreen(onCollectionClick: (Collection) -> Unit) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val viewModel: CollectionsViewModel = viewModel(
        factory = CollectionsViewModelFactory(application)
    )

    val collections by viewModel.collections.collectAsState()
    val favourites by viewModel.favourites.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }
    var newCollectionName by remember { mutableStateOf("") }

    var showRenameDialog by remember { mutableStateOf(false) }
    var renameCollectionName by remember { mutableStateOf("") }

    var selectedItemIds by rememberSaveable { mutableStateOf(setOf<Int>()) }
    val selectionMode by remember {
        derivedStateOf { selectedItemIds.isNotEmpty() }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create Collection") },
            text = {
                OutlinedTextField(
                    value = newCollectionName,
                    onValueChange = { newCollectionName = it },
                    label = { Text("Name") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newCollectionName.isNotBlank()) {
                        viewModel.createCollection(newCollectionName)
                    }
                    showCreateDialog = false
                    newCollectionName = ""
                }) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCreateDialog = false
                    newCollectionName = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showRenameDialog && selectedItemIds.size == 1) {
        val selectedId = selectedItemIds.first()
        val currentName = collections.find { it.id == selectedId }?.name ?: ""
        if (renameCollectionName.isEmpty()) {
            renameCollectionName = currentName
        }

        AlertDialog(
            onDismissRequest = {
                showRenameDialog = false
                renameCollectionName = ""
            },
            title = { Text("Rename Collection") },
            text = {
                OutlinedTextField(
                    value = renameCollectionName,
                    onValueChange = { renameCollectionName = it },
                    label = { Text("New Name") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (renameCollectionName.isNotBlank() && renameCollectionName != currentName) {
                        viewModel.renameCollection(selectedId, renameCollectionName)
                    }
                    showRenameDialog = false
                    renameCollectionName = ""
                    selectedItemIds = emptySet()
                }) {
                    Text("Rename")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showRenameDialog = false
                    renameCollectionName = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Collections") }
            )
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
                            if (selectedItemIds.size == 1 && !selectedItemIds.contains(-1)) { // Favourites ID is -1
                                IconButton(onClick = {
                                    showRenameDialog = true
                                }) {
                                    Icon(Edit, contentDescription = "Rename Collection")
                                }
                            }
                            IconButton(onClick = {
                                viewModel.deleteCollections(selectedItemIds)
                                selectedItemIds = emptySet()
                            }) {
                                Icon(Delete, contentDescription = "Delete selected")
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            if (!selectionMode) {
                FloatingActionButton(onClick = { showCreateDialog = true }) {
                    Icon(add, contentDescription = "Create Collection")
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item {
                CollectionCard(
                    collection = favourites ?: Collection(-1, "Favourites", 0),
                    modifier = Modifier
                        .animateItem()
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(280.dp),
                    onClick = {
                        if (!selectionMode) onCollectionClick(
                            favourites ?: Collection(
                                -1,
                                "Favourites",
                                0
                            )
                        )
                    }
                )
            }
            items(collections, key = { it.id }) { collection ->
                val isSelected = selectedItemIds.contains(collection.id)
                CollectionCard(
                    collection = collection,
                    isSelected = isSelected,
                    modifier = Modifier
                        .animateItem()
                        .padding(16.dp)
                        .fillMaxWidth()
                        .height(280.dp),
                    onClick = {
                        if (selectionMode) {
                            selectedItemIds = if (isSelected) {
                                selectedItemIds - collection.id
                            } else {
                                selectedItemIds + collection.id
                            }
                        } else {
                            onCollectionClick(collection)
                        }
                    },
                    onLongClick = {
                        if (!selectionMode) {
                            selectedItemIds = selectedItemIds + collection.id
                        }
                    }
                )
            }
        }
    }
}