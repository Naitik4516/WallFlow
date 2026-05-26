package com.ns.wallflow.model

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


@Serializable
data class Wallpaper(
    val id: Int,
    val filePath: String,
    val collection: String? = null,
    val addedAt: Long = System.currentTimeMillis(),
    val timePhase: WallpaperTimePhase,
    val brightness: WallpaperBrightness,
    val isFavourite: Boolean = false,
    val originalUri: String = ""
): NavKey
