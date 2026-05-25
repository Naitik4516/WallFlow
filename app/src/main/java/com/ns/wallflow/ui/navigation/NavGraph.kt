package com.ns.wallflow.ui.navigation

import kotlinx.serialization.Serializable
import androidx.navigation3.runtime.NavKey
import androidx.compose.runtime.Immutable
import com.ns.wallflow.model.Collection
import com.ns.wallflow.model.Wallpaper
import androidx.navigation3.runtime.NavBackStack
@Immutable
sealed interface Screen: NavKey {
    @Serializable data object Home : Screen
    @Serializable data class Preview(val wallpaper: Wallpaper) : Screen

    @Serializable data object Collections : Screen
    @Serializable data class CollectionWallpapers(val collection: Collection) : Screen

    @Serializable data object Settings : Screen
}

class AppNavigator(private val backStack: NavBackStack<NavKey>) {

    // Tracks time to prevent rapid double-clicks from pushing duplicate screens
    private var lastNavTime = 0L
    private fun safeNavigate(action: () -> Unit) {
        val now = System.currentTimeMillis()
        if (now - lastNavTime > 500L) {
            lastNavTime = now
            action()
        }
    }

    fun navigateTo(screen: Screen) = safeNavigate {
        backStack.add(screen)
    }

    fun pop() {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }

    fun replaceAll(screen: Screen) = safeNavigate {
        backStack.clear()
        backStack.add(screen)
    }
}