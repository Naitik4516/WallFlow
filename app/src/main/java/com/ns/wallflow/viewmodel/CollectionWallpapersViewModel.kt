package com.ns.wallflow.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ns.wallflow.data.AppDatabase
import com.ns.wallflow.data.toWallpaper
import com.ns.wallflow.model.Wallpaper
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CollectionWallpapersViewModel(
    application: Application,
    private val collectionId: Int,
    private val collectionName: String
) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val collectionDao = db.collectionDao()
    private val wallpaperDao = db.wallpaperDao()

    private val _wallpapers = MutableStateFlow<List<Wallpaper>>(emptyList())
    val wallpapers: StateFlow<List<Wallpaper>> = _wallpapers

    private val _allOtherWallpapers = MutableStateFlow<List<Wallpaper>>(emptyList())
    val allOtherWallpapers: StateFlow<List<Wallpaper>> = _allOtherWallpapers

    private var favouritesJob: Job? = null

    init {
        loadWallpapers()
        loadOtherWallpapers()
    }

    fun loadWallpapers() {
        favouritesJob?.cancel()
        if (collectionId == -1) {
            favouritesJob = viewModelScope.launch {
                wallpaperDao.getFavouriteWallpapersFlow().collect { entities ->
                    _wallpapers.value = entities.map { it.toWallpaper("Favourites") }
                }
            }
        } else {
            viewModelScope.launch {
                val cw = collectionDao.getCollectionWithWallpapers(collectionId)
                val mapped = cw?.wallpapers?.map { it.toWallpaper(collectionName) } ?: emptyList()
                _wallpapers.value = mapped
            }
        }
    }

    fun loadOtherWallpapers() {
        viewModelScope.launch {
            val all = wallpaperDao.getAllWallpapers()
            if (collectionId == -1) {
                _allOtherWallpapers.value = all
                    .filter { !it.isFavourite }
                    .map { it.toWallpaper() }
            } else {
                _allOtherWallpapers.value = all
                    .filter { it.collectionId != collectionId }
                    .map { it.toWallpaper() }
            }
        }
    }

    fun removeWallpapersFromCollection(wallpaperIds: Set<Int>, collectionId: Int) {
        viewModelScope.launch {
            if (collectionId == -1) {
                wallpaperDao.updateFavouriteStatusForIds(wallpaperIds.toList(), false)
            } else {
                collectionDao.assignWallpapersToCollection(wallpaperIds.toList(), null)
            }
            loadWallpapers()
            loadOtherWallpapers()
        }
    }

    fun addWallpapersToCollection(wallpaperIds: Set<Int>) {
        viewModelScope.launch {
            if (collectionId == -1) {
                wallpaperDao.updateFavouriteStatusForIds(wallpaperIds.toList(), true)
            } else {
                collectionDao.assignWallpapersToCollection(wallpaperIds.toList(), collectionId)
            }
            loadWallpapers()
            loadOtherWallpapers()
        }
    }

    fun deleteWallpapers(wallpaperIds: Set<Int>) {
        viewModelScope.launch {
            wallpaperDao.deleteWallpapersByIds(wallpaperIds.toList())
            loadWallpapers()
            loadOtherWallpapers()
        }
    }
}

class CollectionWallpapersViewModelFactory(
    private val application: Application,
    private val collectionId: Int,
    private val collectionName: String
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectionWallpapersViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CollectionWallpapersViewModel(application, collectionId, collectionName) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
