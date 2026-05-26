package com.ns.wallflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ns.wallflow.data.settingsDataStore
import com.ns.wallflow.model.AppTheme
import com.ns.wallflow.ui.navigation.AppNavigationHost
import com.ns.wallflow.ui.theme.WallFlowTheme
import kotlinx.coroutines.flow.map

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val appThemeState = applicationContext.settingsDataStore.data
                .map { it.theme }
                .collectAsStateWithLifecycle(initialValue = AppTheme.SYSTEM)

            App(appThemeState.value)
        }
    }
}

@Composable
fun App(theme: AppTheme = AppTheme.SYSTEM) {
    WallFlowTheme(appTheme = theme) {
        AppNavigationHost()
    }
}

@Preview
@Composable
fun MainActivityPreview() {
    App()
}