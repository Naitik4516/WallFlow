package com.ns.wallflow.model

import kotlinx.serialization.Serializable

enum class AppTheme { LIGHT, DARK, SYSTEM, DYNAMIC }
enum class WallpaperTarget { HOME_SCREEN, LOCK_SCREEN, BOTH }
enum class WallpaperMode { TIME, DAY_NIGHT, DAY_CYCLES, WEEKLY, SYSTEM_THEME }
enum class TriggerFrequency(val label: String) {
    MIN_15("15 Minutes"), HOUR_1("1 Hour"), HOUR_4("4 Hours"),
    HOUR_8("8 Hours"), HOUR_12("12 Hours"), DAILY("Daily")
}

@Serializable
sealed class WallpaperType {
    abstract val label: String

    @Serializable
    data object Any : WallpaperType() {
        override val label = "Any"
    }

    @Serializable
    data class Collection(
        val collectionId: Int,
        val collectionName: String,
        val thumbnailPath: String? = null
    ) : WallpaperType() {
        override val label = "Collection ($collectionName)"
    }

    @Serializable
    data class Specific(
        val wallpaperId: Int,
        val wallpaperName: String,
        val thumbnailPath: String? = null
    ) : WallpaperType() {
        override val label = "Specific Wallpaper"
    }
}

@Serializable
data class AutoWallpaperSettings(
    val isEnabled: Boolean = false,
    val target: WallpaperTarget = WallpaperTarget.BOTH,
    val mode: WallpaperMode = WallpaperMode.TIME,
    val timeFrequency: TriggerFrequency = TriggerFrequency.HOUR_1,
    val timeWallpaperType: WallpaperType = WallpaperType.Any,
    val dayWallpaperType: WallpaperType = WallpaperType.Any,
    val nightWallpaperType: WallpaperType = WallpaperType.Any,
    // Maps storing configurations for complex cycles (Key -> WallpaperType)
    val dayCyclesConfig: Map<String, WallpaperType> = mapOf(
        "Morning" to WallpaperType.Any, "Afternoon" to WallpaperType.Any,
        "Evening" to WallpaperType.Any, "Night" to WallpaperType.Any
    ),
    val weeklyConfig: Map<String, WallpaperType> = mapOf(
        "Monday" to WallpaperType.Any,
        "Tuesday" to WallpaperType.Any,
        "Wednesday" to WallpaperType.Any,
        "Thursday" to WallpaperType.Any,
        "Friday" to WallpaperType.Any,
        "Saturday" to WallpaperType.Any,
        "Sunday" to WallpaperType.Any
    ),
    val systemLightWallpaperType: WallpaperType = WallpaperType.Any,
    val systemDarkWallpaperType: WallpaperType = WallpaperType.Any
)

@Serializable
data class AppSettingsState(
    val theme: AppTheme = AppTheme.DYNAMIC,
    val autoUpdate: Boolean = true,
    val useSeparateConfig: Boolean = false,
    val autoWallpaper: AutoWallpaperSettings = AutoWallpaperSettings(),
    val homeAutoWallpaper: AutoWallpaperSettings = AutoWallpaperSettings(target = WallpaperTarget.HOME_SCREEN),
    val lockAutoWallpaper: AutoWallpaperSettings = AutoWallpaperSettings(target = WallpaperTarget.LOCK_SCREEN),
    val optimizeWallpaper: Boolean = true,
    val autoAddTags: Boolean = true
)
