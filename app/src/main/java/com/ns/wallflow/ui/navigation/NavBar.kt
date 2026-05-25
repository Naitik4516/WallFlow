package com.ns.wallflow.ui.navigation

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import com.ns.wallflow.ui.icons.cards
import com.ns.wallflow.ui.icons.Home
import com.ns.wallflow.ui.icons.Settings


@Composable
fun NavBar(
    selectedItem: NavKey?,
    modifier: Modifier = Modifier,
    onItemSelected: (Screen) -> Unit
) {
    NavigationBar(modifier = modifier) {
        NavigationBarItem(
            icon = { Icon(Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedItem == Screen.Home,
            onClick = { onItemSelected(Screen.Home) }
        )
        NavigationBarItem(
            icon = { Icon(cards, contentDescription = "Collections") },
            label = { Text("Collections") },
            selected = selectedItem is Screen.Collections,
            onClick = { onItemSelected(Screen.Collections) }
        )
        NavigationBarItem(
            icon = { Icon(Settings, contentDescription = "Settings") },
            label = { Text("Settings") },
            selected = selectedItem == Screen.Settings,
            onClick = { onItemSelected(Screen.Settings) }
        )
    }
}
