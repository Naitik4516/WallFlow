package com.ns.wallflow.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.get
import androidx.core.graphics.scale
import com.ns.wallflow.data.AppDatabase
import com.ns.wallflow.data.WallpaperDao
import com.ns.wallflow.data.WallpaperEntity
import com.ns.wallflow.data.settingsDataStore
import com.ns.wallflow.model.WallpaperBrightness
import com.ns.wallflow.model.WallpaperTimePhase
import com.ns.wallflow.ui.icons.add
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

data class WallpaperTags(
    val timePhase: WallpaperTimePhase,   // MORNING, AFTERNOON, EVENING, NIGHT
    val brightness: WallpaperBrightness   // LIGHT, DARK
)

@Composable
fun WallpaperImportBtn(
    isImporting: Boolean,
    collectionId: Int? = null,
    onImportStateChanged: (Boolean, Int, Int) -> Unit
){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uris ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uris.isNotEmpty()) {
            Log.d("PhotoPicker", "Selected URI: $uris")
            onImportStateChanged(true, 0, uris.size)
            scope.launch {
                val dao = AppDatabase.getDatabase(context.applicationContext, this).wallpaperDao()
                for ((index, uri) in uris.withIndex()) {
                    saveAndClassifyWallpaper(context, uri, dao, collectionId)
                    onImportStateChanged(true, index + 1, uris.size)
                }
                onImportStateChanged(false, 0, 0)
            }
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    FloatingActionButton(onClick = {
        if (!isImporting) {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }) {
        Icon(add, contentDescription = "Import Wallpaper")
    }
}

suspend fun saveAndClassifyWallpaper(context: Context, uri: Uri, dao: WallpaperDao, collectionId: Int? = null) {
    withContext(Dispatchers.IO) {
        try {
            val settings = context.settingsDataStore.data.first()
            val optimize = settings.optimizeWallpaper
            val autoAddTags = settings.autoAddTags

            // 1. Decode URI to Bitmap safely
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return@withContext
            inputStream?.close()

            // 2. Classify image across both levels at once
            val tags = if (autoAddTags) {
                analyzeAndExtractTags(originalBitmap)
            } else {
                WallpaperTags(WallpaperTimePhase.MORNING, WallpaperBrightness.LIGHT)
            }
            Log.d("WallpaperImport", "Classified -> TimePhase: ${tags.timePhase}, Tone: ${tags.brightness}")

            // 3. Prevent duplicate imports based on uri
            val existingCount = dao.getCountByOriginalUri(uri.toString())
            if (existingCount > 0) {
                Log.d("WallpaperImport", "Skipped duplicate import: $uri")
                return@withContext
            }

            // 4. Setup the private internal storage directory path
            val targetDirectory = File(context.filesDir, "wallpapers").apply {
                if (!exists()) mkdirs()
            }
            val destinationFile = File(targetDirectory, "wp_${UUID.randomUUID()}.webp")

            // 5. Compress to lossy WebP at 90% Quality or copy original
            if (optimize) {
                FileOutputStream(destinationFile).use { outputStream ->
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        originalBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 90, outputStream)
                    } else {
                        @Suppress("DEPRECATION")
                        originalBitmap.compress(Bitmap.CompressFormat.WEBP, 90, outputStream)
                    }
                }
            } else {
                // If not optimizing, save as PNG at 100% to preserve quality without WebP optimizations
                FileOutputStream(destinationFile).use { outputStream ->
                    originalBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
            }

            // 6. Insert rows directly into Room with both tags mapped
            val entity = WallpaperEntity(
                filePath = destinationFile.absolutePath,
                timePhaseTag = tags.timePhase.name,
                brightnessTag = tags.brightness.name,
                collectionId = collectionId,
                originalUri = uri.toString()
            )
            dao.insertWallpaper(entity)

            Log.d("WallpaperImport", "Import complete. Saved to database.")
            originalBitmap.recycle() // Free memory footprint

        } catch (e: Exception) {
            Log.e("WallpaperImport", "Failed to process import job", e)
        }
    }
}

private fun analyzeAndExtractTags(bitmap: Bitmap): WallpaperTags {
    val smallBitmap = bitmap.scale(50, 50, false)

    val width = smallBitmap.width
    val height = smallBitmap.height
    val totalPixels = width * height

    var totalBrightness = 0f
    var totalSaturation = 0f

    var warmPixelCount = 0
    var bluePixelCount = 0
    var darkPixelCount = 0

    val hsv = FloatArray(3)

    for (y in 0 until height) {
        for (x in 0 until width) {

            val pixel = smallBitmap[x, y]

            Color.colorToHSV(pixel, hsv)

            val hue = hsv[0]         // 0 - 360
            val saturation = hsv[1] // 0.0 - 1.0
            val value = hsv[2]      // 0.0 - 1.0

            totalBrightness += value
            totalSaturation += saturation

            // =====================================================
            // EVENING / SUNSET DETECTION
            // Reds, oranges, magentas
            // =====================================================

            if ((hue in 0f..45f || hue in 315f..360f)
                && saturation > 0.4f
            ) {
                warmPixelCount++
            }

            // =====================================================
            // MORNING SKY DETECTION
            // Blue sky tones
            // =====================================================

            if ((hue in 180f..260f)
                && saturation > 0.25f
                && value > 0.4f
            ) {
                bluePixelCount++
            }

            // =====================================================
            // VERY DARK PIXELS
            // Better night detection
            // =====================================================

            if (value < 0.18f) {
                darkPixelCount++
            }
        }
    }

    smallBitmap.recycle()

    val avgBrightness = totalBrightness / totalPixels
    val avgSaturation = totalSaturation / totalPixels

    val warmRatio = warmPixelCount.toFloat() / totalPixels
    val blueRatio = bluePixelCount.toFloat() / totalPixels
    val darkRatio = darkPixelCount.toFloat() / totalPixels


    val brightnessTag = if (avgBrightness < 0.45f) WallpaperBrightness.DARK else WallpaperBrightness.LIGHT

    val timePhaseTag = when {
        avgBrightness < 0.25f || darkRatio > 0.55f -> {
            WallpaperTimePhase.NIGHT
        }

        warmRatio > 0.12f ||
                (warmRatio > 0.07f && avgSaturation > 0.42f) -> {
            WallpaperTimePhase.EVENING
        }

        avgBrightness > 0.72f &&
                blueRatio < 0.35f -> {
            WallpaperTimePhase.AFTERNOON
        }

        else -> {
            WallpaperTimePhase.MORNING
        }
    }

    return WallpaperTags(
        timePhase = timePhaseTag,
        brightness = brightnessTag
    )
}