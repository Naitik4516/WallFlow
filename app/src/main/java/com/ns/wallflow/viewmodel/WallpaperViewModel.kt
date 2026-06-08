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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WallpaperViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val wallpaperDao = db.wallpaperDao()

    private data class FilterState(val value: String = "All", val type: String = "time")

    private val _filterState = MutableStateFlow(FilterState())
    val selectedFilter: StateFlow<String> = _filterState
        .map { it.value }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "All")


    @OptIn(ExperimentalCoroutinesApi::class)
    val wallpapers: StateFlow<List<Wallpaper>> = _filterState
        .flatMapLatest { filterState ->
            if (filterState.value == "All") {
                wallpaperDao.getAllWallpapersFlow()
            } else {
                wallpaperDao.getWallpapersByTagFlow(filterState.value.uppercase())
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
        _filterState.value = FilterState(filter, type)
    }

    fun deleteWallpapers(wallpaperIds: Set<Int>) {
        viewModelScope.launch {
            wallpaperDao.deleteWallpapersByIds(wallpaperIds.toList())
        }
    }

    fun updateWallpaperTags(wallpaperId: Int, tags: List<String>) {
        viewModelScope.launch {
            wallpaperDao.updateWallpaperTags(wallpaperId, tags.joinToString(","))
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
