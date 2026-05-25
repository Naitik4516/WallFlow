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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import com.ns.wallflow.data.CollectionEntity

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application, viewModelScope)
    private val wallpaperDao = db.wallpaperDao()
    private val collectionDao = db.collectionDao()

    private val _selectedFilter = MutableStateFlow("All")
    private val _filterType = MutableStateFlow("time") // "time", "brightness"
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _collections = MutableStateFlow<List<CollectionEntity>>(emptyList())
    val collections: StateFlow<List<CollectionEntity>> = _collections.asStateFlow()

    init {
        loadCollections()
    }

    fun loadCollections() {
        viewModelScope.launch {
            _collections.value = collectionDao.getAllCollections()
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val wallpapers: StateFlow<List<Wallpaper>> = _selectedFilter
        .flatMapLatest { filter ->
            if (filter == "All") {
                wallpaperDao.getAllWallpapersFlow()
            } else {
                when (_filterType.value) {
                    "time" -> wallpaperDao.getWallpapersByTimePhaseFlow(filter.uppercase())
                    "brightness" -> wallpaperDao.getWallpapersByBrightnessFlow(filter.uppercase())
                    else -> wallpaperDao.getAllWallpapersFlow()
                }
            }
        }
        .map { entities -> entities.map { it.toWallpaper() } }
        .catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setFilter(filter: String, type: String) {
        _selectedFilter.value = filter
        _filterType.value = type
    }

    fun deleteWallpaper(wallpaper: Wallpaper) {
        viewModelScope.launch {
            wallpaperDao.deleteWallpaperById(wallpaper.id)
        }
    }

    fun addWallpaperToCollection(wallpaper: Wallpaper, collectionId: Int) {
        viewModelScope.launch {
            collectionDao.assignWallpaperToCollection(wallpaper.id, collectionId)
        }
    }
}

class HomeViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
