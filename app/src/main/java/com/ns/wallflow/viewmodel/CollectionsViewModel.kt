package com.ns.wallflow.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ns.wallflow.data.AppDatabase
import com.ns.wallflow.data.CollectionEntity
import com.ns.wallflow.model.Collection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CollectionsViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val collectionDao = db.collectionDao()
    private val wallpaperDao = db.wallpaperDao()

    private val _collections = MutableStateFlow<List<Collection>>(emptyList())
    val collections: StateFlow<List<Collection>> = _collections

    private val _favourites = MutableStateFlow<Collection?>(null)
    val favourites: StateFlow<Collection?> = _favourites

    init {
        observeCollections()
        observeFavourites()
    }

    private fun observeCollections() {
        viewModelScope.launch {
            collectionDao.getCollectionsWithWallpapersFlow().collect { collectionsWithWallpapers ->
                _collections.value = collectionsWithWallpapers.map { entry ->
                    val wallpapers = entry.wallpapers
                    val coverPaths = wallpapers.take(3).map { it.filePath }
                    Collection(
                        id = entry.collection.id,
                        name = entry.collection.name,
                        totalWallpapers = wallpapers.size,
                        coverPaths
                    )
                }
            }
        }
    }

    private fun observeFavourites() {
        viewModelScope.launch {
            wallpaperDao.getFavouriteWallpapersFlow().collect { favWallpapers ->
                val coverPaths = favWallpapers.take(3).map { it.filePath }
                _favourites.value = Collection(
                    id = -1,
                    name = "Favourites",
                    totalWallpapers = favWallpapers.size,
                    coverPaths
                )
            }
        }
    }

    fun createCollection(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                collectionDao.createCollection(CollectionEntity(name = name))
            }
        }
    }

    fun deleteCollections(ids: Set<Int>) {
        viewModelScope.launch {
            collectionDao.deleteCollectionsByIds(ids.toList())
        }
    }

    fun renameCollection(id: Int, newName: String) {
        viewModelScope.launch {
            if (newName.isNotBlank()) {
                collectionDao.renameCollection(id, newName)
            }
        }
    }

    fun addWallpapersToCollection(wallpaperIds: Set<Int>, collectionId: Int) {
        viewModelScope.launch {
            collectionDao.assignWallpapersToCollection(wallpaperIds.toList(), collectionId)
        }
    }
}

class CollectionsViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectionsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CollectionsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
