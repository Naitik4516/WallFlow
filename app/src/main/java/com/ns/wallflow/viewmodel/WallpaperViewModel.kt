package com.ns.wallflow.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ns.wallflow.data.AppDatabase
import com.ns.wallflow.data.toWallpaper
import com.ns.wallflow.model.Wallpaper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WallpaperViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application, viewModelScope)
    private val wallpaperDao = db.wallpaperDao()

    private val _selectedFilter = MutableStateFlow("All")
    private val _filterType = MutableStateFlow("time") // "time", "brightness"
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()


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

    fun deleteWallpapers(wallpaperIds: Set<Int>) {
        viewModelScope.launch {
            wallpaperDao.deleteWallpapersByIds(wallpaperIds.toList())
        }
    }


    fun deleteWallpaper(wallpaper: Wallpaper) {
        viewModelScope.launch {
            wallpaperDao.deleteWallpaperById(wallpaper.id)
        }
    }
}

class WallpaperViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WallpaperViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WallpaperViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
