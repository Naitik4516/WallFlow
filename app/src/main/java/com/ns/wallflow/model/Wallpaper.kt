package com.ns.wallflow.model

import kotlinx.serialization.Serializable
import androidx.navigation3.runtime.NavKey


@Serializable
data class Wallpaper(
    val id: Int,
    val filePath: String,
    val collection: String? = null,
    val addedAt: Long = System.currentTimeMillis(),
    val timePhase: WallpaperTimePhase,
    val brightness: WallpaperBrightness,
): NavKey
