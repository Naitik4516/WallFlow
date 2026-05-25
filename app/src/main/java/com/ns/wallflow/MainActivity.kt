package com.ns.wallflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ns.wallflow.ui.theme.WallFlowTheme
import com.ns.wallflow.ui.navigation.AppNavigationHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}

@Composable
fun App() {
    WallFlowTheme {
        AppNavigationHost()
    }
}

@Preview
@Composable
fun MainActivityPreview() {
    App()
}