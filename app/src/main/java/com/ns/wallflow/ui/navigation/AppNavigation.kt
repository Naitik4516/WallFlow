package com.ns.wallflow.ui.navigation

import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import com.ns.wallflow.ui.screens.CollectionWallpapersScreen
import com.ns.wallflow.ui.screens.CollectionsScreen
import com.ns.wallflow.ui.screens.HomeScreen
import com.ns.wallflow.ui.screens.PreviewScreen
import com.ns.wallflow.ui.screens.SettingsScreen

@Composable
fun AppNavigationHost() {
    val backStack = rememberNavBackStack(Screen.Home)
    val navigator = remember(backStack) { AppNavigator(backStack) }
    val currentScreen = backStack.lastOrNull()

    val colors = MaterialTheme.colorScheme

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentScreen !is Screen.Preview) {
                NavBar(
                    currentScreen
                ) {
                    if (it == Screen.Home) navigator.replaceAll(Screen.Home)
                    navigator.navigateTo(it)
                }
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier
            .padding(innerPadding)
            .background(
                Brush.verticalGradient(
                    listOf(colors.primary, colors.secondary)
                )
            )
            .fillMaxSize()) {
            SharedTransitionLayout {
                NavDisplay(
                    backStack = backStack,
                    onBack = { navigator.pop() },
                    // Essential for Production: Decorators ensure state preservation & lifecycle tracking
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    transitionSpec = {
                        slideInHorizontally(
                            animationSpec = tween(300),
                            initialOffsetX = { fullWidth -> fullWidth }
                        ) togetherWith slideOutHorizontally(
                            animationSpec = tween(300),
                            targetOffsetX = { fullWidth -> -fullWidth }
                        )
                    },
                    popTransitionSpec = {
                        slideInHorizontally(
                            animationSpec = tween(300),
                            initialOffsetX = { fullWidth -> -fullWidth }
                        ) togetherWith slideOutHorizontally(
                            animationSpec = tween(300),
                            targetOffsetX = { fullWidth -> fullWidth }
                        )
                    },
                    entryProvider = entryProvider {
                        entry<Screen.Home> {
                            HomeScreen(
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                onWallpaperClick = { wallpaper ->
                                    navigator.navigateTo(Screen.Preview(wallpaper))
                                }
                            )
                        }
                        entry<Screen.Collections> {
                            CollectionsScreen(
                                onCollectionClick = { collection ->
                                    navigator.navigateTo(Screen.CollectionWallpapers(collection))
                                }
                            )
                        }
                        entry<Screen.CollectionWallpapers> { screenData ->
                            CollectionWallpapersScreen(
                                collection = screenData.collection,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                onBack = { navigator.pop() },
                                onWallpaperClick = { wallpaper ->
                                    navigator.navigateTo(Screen.Preview(wallpaper))
                                }
                            )
                        }
                        entry<Screen.Preview> { previewScreen ->
                            PreviewScreen(
                                previewScreen.wallpaper,
                                sharedTransitionScope = this@SharedTransitionLayout,
                                animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                            ) { navigator.pop() }
                        }
                        entry<Screen.Settings> {
                            SettingsScreen()
                        }
                    }
                )
            }
        }
    }
}