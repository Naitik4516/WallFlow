package com.ns.wallflow.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ns.wallflow.data.AppDatabase
import com.ns.wallflow.model.Wallpaper
import com.ns.wallflow.data.toWallpaper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CollectionWallpapersViewModel(
    application: Application,
    private val collectionId: Int,
    private val collectionName: String
) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application, viewModelScope)
    private val collectionDao = db.collectionDao()

    private val _wallpapers = MutableStateFlow<List<Wallpaper>>(emptyList())
    val wallpapers: StateFlow<List<Wallpaper>> = _wallpapers

    init {
        loadWallpapers()
    }

    fun loadWallpapers() {
        viewModelScope.launch {
            val cw = collectionDao.getCollectionWithWallpapers(collectionId)
            val mapped = cw?.wallpapers?.map { it.toWallpaper(collectionName) } ?: emptyList()
            _wallpapers.value = mapped
        }
    }

    fun removeWallpapersFromCollection(wallpaperIds: Set<Int>, collectionId: Int) {
        viewModelScope.launch {
            wallpaperIds.forEach { id ->
                collectionDao.assignWallpaperToCollection(id, collectionId)
            }
            loadWallpapers()
        }
    }

    fun deleteWallpapers(wallpaperIds: Set<Int>) {
        viewModelScope.launch {
            val db = AppDatabase.getDatabase(getApplication(), viewModelScope)
            wallpaperIds.forEach { id ->
                db.wallpaperDao().deleteWallpaperById(id)
            }
            loadWallpapers()
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
