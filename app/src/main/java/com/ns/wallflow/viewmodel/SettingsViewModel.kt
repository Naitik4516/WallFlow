package com.ns.wallflow.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.ns.wallflow.data.settingsDataStore
import com.ns.wallflow.model.AppSettingsState
import com.ns.wallflow.model.TriggerFrequency
import com.ns.wallflow.worker.AutoWallpaperWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application
    private val dataStore = application.settingsDataStore

    val settings: StateFlow<AppSettingsState> = dataStore.data
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppSettingsState()
        )

    fun updateSettings(update: (AppSettingsState) -> AppSettingsState) {
        viewModelScope.launch {
            val updated = dataStore.updateData(update)
            scheduleAutoWallpaper(updated)
        }
    }

    private fun scheduleAutoWallpaper(settings: AppSettingsState) {
        val workManager = WorkManager.getInstance(app)

        if (!settings.autoWallpaper.isEnabled) {
            workManager.cancelUniqueWork("AutoWallpaperWork")
            return
        }

        val repeatInterval = when (settings.autoWallpaper.timeFrequency) {
            TriggerFrequency.MIN_15 -> 15L
            TriggerFrequency.HOUR_1 -> 1L * 60L
            TriggerFrequency.HOUR_4 -> 4L * 60L
            TriggerFrequency.HOUR_8 -> 8L * 60L
            TriggerFrequency.HOUR_12 -> 12L * 60L
            TriggerFrequency.DAILY -> 24L * 60L
        }

        // Use Constraints if needed
        val constraints = Constraints.Builder().build()

        val workRequest =
            PeriodicWorkRequestBuilder<AutoWallpaperWorker>(repeatInterval, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()

        workManager.enqueueUniquePeriodicWork(
            "AutoWallpaperWork",
            ExistingPeriodicWorkPolicy.UPDATE, // or UPDATE (Android 12+) or REPLACE
            workRequest
        )
    }
}

class SettingsViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}