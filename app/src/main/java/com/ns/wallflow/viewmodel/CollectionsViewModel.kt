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
    private val db = AppDatabase.getDatabase(application, viewModelScope)
    private val collectionDao = db.collectionDao()

    private val _collections = MutableStateFlow<List<Collection>>(emptyList())
    val collections: StateFlow<List<Collection>> = _collections

    init {
        loadCollections()
    }

    fun loadCollections() {
        viewModelScope.launch {
            val entities = collectionDao.getAllCollections()
            val mapped = entities.map { entity ->
                val cw = collectionDao.getCollectionWithWallpapers(entity.id)
                val wallpapers = cw?.wallpapers ?: emptyList()
                val coverPath = wallpapers.firstOrNull()?.filePath ?: ""
                Collection(
                    id = entity.id,
                    name = entity.name,
                    totalWallpapers = wallpapers.size,
                    coverImagePath = coverPath
                )
            }
            _collections.value = mapped
        }
    }

    fun createCollection(name: String) {
        viewModelScope.launch {
            if (name.isNotBlank()) {
                collectionDao.createCollection(CollectionEntity(name = name))
                loadCollections()
            }
        }
    }

    fun deleteCollections(ids: Set<Int>) {
        viewModelScope.launch {
            collectionDao.deleteCollectionsByIds(ids.toList())
            loadCollections()
        }
    }

    fun renameCollection(id: Int, newName: String) {
        viewModelScope.launch {
            if (newName.isNotBlank()) {
                collectionDao.renameCollection(id, newName)
                loadCollections()
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
