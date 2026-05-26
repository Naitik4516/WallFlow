package com.ns.wallflow.worker

import android.app.WallpaperManager
import android.content.Context
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ns.wallflow.data.AppDatabase
import com.ns.wallflow.data.settingsDataStore
import com.ns.wallflow.model.AutoWallpaperSettings
import com.ns.wallflow.model.WallpaperMode
import com.ns.wallflow.model.WallpaperTarget
import com.ns.wallflow.model.WallpaperType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.first
import java.util.Calendar

class AutoWallpaperWorker(
    private val ctx: Context,
    params: WorkerParameters
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        return try {
            val settings = ctx.settingsDataStore.data.first().autoWallpaper
            if (!settings.isEnabled) return Result.success()

            val wallpaperTypeToApply: WallpaperType = determineWallpaperType(settings)

            val dao = AppDatabase.getDatabase(ctx, GlobalScope).wallpaperDao()
            val wallpaperFile = when (wallpaperTypeToApply) {
                is WallpaperType.Any -> dao.getRandomWallpaper()
                is WallpaperType.Collection -> dao.getRandomWallpaperFromCollection(
                    wallpaperTypeToApply.collectionId
                )

                is WallpaperType.Specific -> dao.getWallpaperById(wallpaperTypeToApply.wallpaperId)
            }

            if (wallpaperFile != null) {
                applyWallpaper(wallpaperFile.filePath, settings.target)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("AutoWallpaperWorker", "Error applying wallpaper", e)
            Result.failure()
        }
    }

    private fun determineWallpaperType(settings: AutoWallpaperSettings): WallpaperType {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        return when (settings.mode) {
            WallpaperMode.TIME -> settings.timeWallpaperType
            WallpaperMode.DAY_NIGHT -> {
                if (hour in 6..17) settings.dayWallpaperType else settings.nightWallpaperType
            }

            WallpaperMode.DAY_CYCLES -> {
                when (hour) {
                    in 6..11 -> settings.dayCyclesConfig["Morning"] ?: WallpaperType.Any
                    in 12..17 -> settings.dayCyclesConfig["Afternoon"] ?: WallpaperType.Any
                    in 18..20 -> settings.dayCyclesConfig["Evening"] ?: WallpaperType.Any
                    else -> settings.dayCyclesConfig["Night"] ?: WallpaperType.Any
                }
            }

            WallpaperMode.WEEKLY -> {
                val dayStr = when (dayOfWeek) {
                    Calendar.MONDAY -> "Monday"
                    Calendar.TUESDAY -> "Tuesday"
                    Calendar.WEDNESDAY -> "Wednesday"
                    Calendar.THURSDAY -> "Thursday"
                    Calendar.FRIDAY -> "Friday"
                    Calendar.SATURDAY -> "Saturday"
                    else -> "Sunday"
                }
                settings.weeklyConfig[dayStr] ?: WallpaperType.Any
            }

            WallpaperMode.SYSTEM_THEME -> {
                val currentNightMode =
                    ctx.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                    settings.systemDarkWallpaperType
                } else {
                    settings.systemLightWallpaperType
                }
            }
        }
    }

    private fun applyWallpaper(filePath: String, target: WallpaperTarget) {
        val bitmap = BitmapFactory.decodeFile(filePath) ?: return
        val wallpaperManager = WallpaperManager.getInstance(ctx)

        val flag = when (target) {
            WallpaperTarget.HOME_SCREEN -> WallpaperManager.FLAG_SYSTEM
            WallpaperTarget.LOCK_SCREEN -> WallpaperManager.FLAG_LOCK
            WallpaperTarget.BOTH -> WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
        }

        wallpaperManager.setBitmap(bitmap, null, true, flag)
    }
}
