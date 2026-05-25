package com.ns.wallflow.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.ns.wallflow.model.Wallpaper
import android.app.Activity
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.shape.RoundedCornerShape
import com.ns.wallflow.ui.icons.arrow_back
import kotlinx.coroutines.launch
import android.app.WallpaperManager
import android.content.Context
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import android.graphics.Bitmap

suspend fun setWallpaperAction(
    context: Context,
    wallpaper: Wallpaper,
    flag: Int
): Boolean {
    return withContext(Dispatchers.IO) {
        try {
            val request = ImageRequest.Builder(context)
                .data(wallpaper.filePath)
                .build()
            val result = context.imageLoader.execute(request)
            if (result is SuccessResult) {
                val bitmap = result.image.toBitmap()
                val wallpaperManager = WallpaperManager.getInstance(context)
                if (flag == 0) {
                    wallpaperManager.setBitmap(bitmap)
                } else {
                    wallpaperManager.setBitmap(bitmap, null, true, flag)
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

suspend fun shareWallpaperWithOtherApp(
    context: Context,
    wallpaper: Wallpaper
) {
    withContext(Dispatchers.IO) {
        try {
            val request = ImageRequest.Builder(context)
                .data(wallpaper.filePath)
                .build()
            val result = context.imageLoader.execute(request)
            if (result is SuccessResult) {
                val bitmap = result.image.toBitmap()
                val cachePath = File(context.cacheDir, "images")
                cachePath.mkdirs()
                val file = File(cachePath, "shared_wallpaper.png")
                val stream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                stream.close()

                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

                val intent = Intent(Intent.ACTION_ATTACH_DATA).apply {
                    addCategory(Intent.CATEGORY_DEFAULT)
                    setDataAndType(uri, "image/png")
                    putExtra("mimeType", "image/png")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }

                withContext(Dispatchers.Main) {
                    context.startActivity(Intent.createChooser(intent, "Set as:"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Failed to share wallpaper", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(
    wallpaper: Wallpaper,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackClick: () -> Unit
) {
    var showUIControls by remember { mutableStateOf(true) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var isSettingWallpaper by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    BackHandler {
        if (showBottomSheet) {
            scope.launch { sheetState.hide() }.invokeOnCompletion { showBottomSheet = false }
        } else {
            onBackClick()
        }
    }

    val view = LocalView.current
    val window = (view.context as? Activity)?.window

    DisposableEffect(showUIControls) {
        val insetsController = window?.let {
            WindowCompat.getInsetsController(it, view)
        }

        if (insetsController != null) {
            insetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            if (showUIControls) {
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            } else {
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
            }
        }

        onDispose {
            insetsController?.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    with(sharedTransitionScope) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { showUIControls = !showUIControls } // Tap screen to toggle full immersion
        ) {
            AsyncImage(
                model = wallpaper.filePath,
                contentDescription = "Fullscreen Wallpaper View",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .sharedElement(
                        rememberSharedContentState(key = "image-${wallpaper.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
            )

            // UI Control Overlay Layer
            AnimatedVisibility(
                visible = showUIControls,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Back Exit Button
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(16.dp)
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .background(Color.DarkGray.copy(alpha = 0.6f), CircleShape)
                            .align(Alignment.TopStart)
                    ) {
                        Icon(arrow_back, contentDescription = "Back", tint = Color.White)
                    }
                    // Bottom Action Sheet Target Space
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .padding(24.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            ElevatedButton (
                                onClick = { showBottomSheet = true },
                                modifier = Modifier.fillMaxWidth(0.6f)
                            ) {
                                Text("Set Wallpaper", fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 6.dp))
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Tap wallpaper for full immersion",
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp, 16.dp)
                        .padding(bottom = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Set Wallpaper",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    FilledTonalButton(
                        onClick = {
                            scope.launch {
                                isSettingWallpaper = true
                                val success = setWallpaperAction(context, wallpaper, WallpaperManager.FLAG_SYSTEM)
                                isSettingWallpaper = false
                                if (success) {
                                    Toast.makeText(context, "Wallpaper set successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to set wallpaper.", Toast.LENGTH_SHORT).show()
                                }
                                sheetState.hide()
                            }.invokeOnCompletion { showBottomSheet = false }
                        },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text("Home Screen", modifier = Modifier.padding(vertical=4.dp))
                    }

                    FilledTonalButton(
                        onClick = {
                            scope.launch {
                                isSettingWallpaper = true
                                val success = setWallpaperAction(context, wallpaper, WallpaperManager.FLAG_LOCK)
                                isSettingWallpaper = false
                                if (success) {
                                    Toast.makeText(context, "Wallpaper set successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to set wallpaper.", Toast.LENGTH_SHORT).show()
                                }
                                sheetState.hide()
                            }.invokeOnCompletion { showBottomSheet = false }
                        },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text("Lock Screen", modifier = Modifier.padding(vertical=4.dp))
                    }

                    FilledTonalButton(
                        onClick = {
                            scope.launch {
                                isSettingWallpaper = true
                                val success = setWallpaperAction(context, wallpaper, 0)
                                isSettingWallpaper = false
                                if (success) {
                                    Toast.makeText(context, "Wallpaper set successfully!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to set wallpaper.", Toast.LENGTH_SHORT).show()
                                }
                                sheetState.hide()
                            }.invokeOnCompletion { showBottomSheet = false }
                        },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text("Home and Lock Screens", modifier = Modifier.padding(vertical=4.dp))
                    }

                    OutlinedButton (
                        onClick = {
                            scope.launch {
                                sheetState.hide()
                                shareWallpaperWithOtherApp(context, wallpaper)
                            }.invokeOnCompletion { showBottomSheet = false }
                        },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Text("Set with ...", modifier = Modifier.padding(vertical=4.dp))
                    }
                }
            }
        }

        if (isSettingWallpaper) {
            Dialog(
                onDismissRequest = { },
                properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Setting Wallpaper...", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
    }
}

//@OptIn(ExperimentalSharedTransitionApi::class)
//@Preview(showBackground = true)
//@Composable
//fun PreviewScreenPreview() {
//    SharedTransitionLayout {
//        AnimatedVisibility(visible = true) {
//            PreviewScreen(
//                wallpaper = Wallpaper(
//                    id = 45,
//                    source = "BUNDLED",
//                    filePath = "file:///android_asset/wallpapers/wallpaper21.webp",
//                    timePhaseTag = "EVENING",
//                    brightnessTag = "DARK"
//                ),
//                sharedTransitionScope = this@SharedTransitionLayout,
//                animatedVisibilityScope = this@AnimatedVisibility,
//                onBackClick = {}
//            )
//        }
//    }
//}
