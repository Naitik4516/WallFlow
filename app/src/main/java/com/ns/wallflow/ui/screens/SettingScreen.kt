package com.ns.wallflow.ui.screens

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ns.wallflow.model.AppTheme
import com.ns.wallflow.model.TriggerFrequency
import com.ns.wallflow.model.WallpaperMode
import com.ns.wallflow.model.WallpaperTarget
import com.ns.wallflow.model.WallpaperType
import com.ns.wallflow.ui.components.CollectionCard
import com.ns.wallflow.viewmodel.CollectionsViewModel
import com.ns.wallflow.viewmodel.CollectionsViewModelFactory
import com.ns.wallflow.viewmodel.SettingsViewModel
import com.ns.wallflow.viewmodel.SettingsViewModelFactory
import com.ns.wallflow.viewmodel.WallpaperViewModel
import com.ns.wallflow.viewmodel.WallpaperViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val settings by viewModel.settings.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Theme
            SettingSectionHeader("Appearance")
            SettingDropdown(
                title = "Theme",
                options = AppTheme.entries,
                selectedOption = settings.theme,
                optionLabel = { it.name.lowercase().replaceFirstChar { char -> char.uppercase() } },
                onOptionSelected = { theme -> viewModel.updateSettings { it.copy(theme = theme) } }
            )

            HorizontalDivider()

            // Auto Update & Tags
            SettingSectionHeader("General")
            SettingSwitch(
                title = "Auto Update",
                description = "Automatically update app content",
                checked = settings.autoUpdate,
                onCheckedChange = { chk -> viewModel.updateSettings { it.copy(autoUpdate = chk) } }
            )
            SettingSwitch(
                title = "Optimize Wallpaper",
                description = "Save wallpapers as WebP with 90% compression",
                checked = settings.optimizeWallpaper,
                onCheckedChange = { chk -> viewModel.updateSettings { it.copy(optimizeWallpaper = chk) } }
            )
            SettingSwitch(
                title = "Automatically Add Tags",
                description = "Auto add brightness and time tags",
                checked = settings.autoAddTags,
                onCheckedChange = { chk -> viewModel.updateSettings { it.copy(autoAddTags = chk) } }
            )

            HorizontalDivider()

            // Auto Wallpaper
            SettingSectionHeader("Auto Wallpaper Change")
            SettingSwitch(
                title = "Enable Auto Wallpaper",
                checked = settings.autoWallpaper.isEnabled,
                onCheckedChange = { checked ->
                    viewModel.updateSettings { s ->
                        s.copy(
                            autoWallpaper = s.autoWallpaper.copy(
                                isEnabled = checked
                            )
                        )
                    }
                }
            )

            AnimatedVisibility(
                visible = settings.autoWallpaper.isEnabled,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(Modifier.padding(start = 16.dp)) {
                    SettingDropdown(
                        title = "Target",
                        options = WallpaperTarget.entries,
                        selectedOption = settings.autoWallpaper.target,
                        optionLabel = {
                            it.name.replace("_", " ").lowercase()
                                .replaceFirstChar { char -> char.uppercase() }
                        },
                        onOptionSelected = { target ->
                            viewModel.updateSettings { s ->
                                s.copy(
                                    autoWallpaper = s.autoWallpaper.copy(
                                        target = target
                                    )
                                )
                            }
                        }
                    )

                    SettingDropdown(
                        title = "Mode",
                        options = WallpaperMode.entries,
                        selectedOption = settings.autoWallpaper.mode,
                        optionLabel = {
                            it.name.replace("_", " ").lowercase()
                                .replaceFirstChar { char -> char.uppercase() }
                        },
                        onOptionSelected = { mode ->
                            viewModel.updateSettings { s ->
                                s.copy(
                                    autoWallpaper = s.autoWallpaper.copy(
                                        mode = mode
                                    )
                                )
                            }
                        }
                    )

                    // Conditional UI based on Mode
                    AnimatedVisibility(
                        visible = settings.autoWallpaper.mode == WallpaperMode.TIME,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column {
                            SettingDropdown(
                                title = "Trigger Frequency",
                                options = TriggerFrequency.entries,
                                selectedOption = settings.autoWallpaper.timeFrequency,
                                optionLabel = { it.label },
                                onOptionSelected = { freq ->
                                    viewModel.updateSettings { s ->
                                        s.copy(
                                            autoWallpaper = s.autoWallpaper.copy(
                                                timeFrequency = freq
                                            )
                                        )
                                    }
                                }
                            )
                            WallpaperSelectionSetting(
                                title = "Wallpaper Set",
                                value = settings.autoWallpaper.timeWallpaperType,
                                onTypeSelected = { type ->
                                    viewModel.updateSettings { s ->
                                        s.copy(
                                            autoWallpaper = s.autoWallpaper.copy(
                                                timeWallpaperType = type
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = settings.autoWallpaper.mode == WallpaperMode.DAY_NIGHT,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column {
                            WallpaperSelectionSetting(
                                title = "Day Wallpaper",
                                value = settings.autoWallpaper.dayWallpaperType,
                                onTypeSelected = { type ->
                                    viewModel.updateSettings { s ->
                                        s.copy(
                                            autoWallpaper = s.autoWallpaper.copy(
                                                dayWallpaperType = type
                                            )
                                        )
                                    }
                                }
                            )
                            WallpaperSelectionSetting(
                                title = "Night Wallpaper",
                                value = settings.autoWallpaper.nightWallpaperType,
                                onTypeSelected = { type ->
                                    viewModel.updateSettings { s ->
                                        s.copy(
                                            autoWallpaper = s.autoWallpaper.copy(
                                                nightWallpaperType = type
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = settings.autoWallpaper.mode == WallpaperMode.DAY_CYCLES,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column {
                            listOf("Morning", "Afternoon", "Evening", "Night").forEach { cycle ->
                                WallpaperSelectionSetting(
                                    title = "$cycle Wallpaper",
                                    value = settings.autoWallpaper.dayCyclesConfig[cycle]
                                        ?: WallpaperType.Any,
                                    onTypeSelected = { type ->
                                        viewModel.updateSettings { s ->
                                            val mutMap =
                                                s.autoWallpaper.dayCyclesConfig.toMutableMap()
                                            mutMap[cycle] = type
                                            s.copy(
                                                autoWallpaper = s.autoWallpaper.copy(
                                                    dayCyclesConfig = mutMap
                                                )
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = settings.autoWallpaper.mode == WallpaperMode.WEEKLY,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column {
                            listOf(
                                "Monday",
                                "Tuesday",
                                "Wednesday",
                                "Thursday",
                                "Friday",
                                "Saturday",
                                "Sunday"
                            ).forEach { day ->
                                WallpaperSelectionSetting(
                                    title = "$day Wallpaper",
                                    value = settings.autoWallpaper.weeklyConfig[day]
                                        ?: WallpaperType.Any,
                                    onTypeSelected = { type ->
                                        viewModel.updateSettings { s ->
                                            val mutMap = s.autoWallpaper.weeklyConfig.toMutableMap()
                                            mutMap[day] = type
                                            s.copy(autoWallpaper = s.autoWallpaper.copy(weeklyConfig = mutMap))
                                        }
                                    }
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = settings.autoWallpaper.mode == WallpaperMode.SYSTEM_THEME,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                    ) {
                        Column {
                            WallpaperSelectionSetting(
                                title = "Light Theme Wallpaper",
                                value = settings.autoWallpaper.systemLightWallpaperType,
                                onTypeSelected = { type ->
                                    viewModel.updateSettings { s ->
                                        s.copy(
                                            autoWallpaper = s.autoWallpaper.copy(
                                                systemLightWallpaperType = type
                                            )
                                        )
                                    }
                                }
                            )
                            WallpaperSelectionSetting(
                                title = "Dark Theme Wallpaper",
                                value = settings.autoWallpaper.systemDarkWallpaperType,
                                onTypeSelected = { type ->
                                    viewModel.updateSettings { s ->
                                        s.copy(
                                            autoWallpaper = s.autoWallpaper.copy(
                                                systemDarkWallpaperType = type
                                            )
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingSwitch(
    title: String,
    description: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            if (description != null) {
                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SettingDropdown(
    title: String,
    options: List<T>,
    selectedOption: T,
    optionLabel: (T) -> String,
    onOptionSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                optionLabel(selectedOption),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            // Hidden anchor to display dropdown anchored here
            Spacer(modifier = Modifier.menuAnchor())
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(optionLabel(option)) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperSelectionSetting(
    title: String,
    value: WallpaperType,
    onTypeSelected: (WallpaperType) -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showSheet = true }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(
                value.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    if (showSheet) {
        WallpaperSelectionBottomSheet(
            title = title,
            currentValue = value,
            onDismiss = { showSheet = false },
            onTypeSelected = {
                onTypeSelected(it)
                showSheet = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperSelectionBottomSheet(
    title: String,
    currentValue: WallpaperType,
    onDismiss: () -> Unit,
    onTypeSelected: (WallpaperType) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var currentStep by remember { mutableStateOf(0) } // 0 = Type Selection, 1 = Pick Collection, 2 = Pick Wallpaper

    val context = LocalContext.current
    val application = context.applicationContext as Application

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        modifier = if (currentStep > 0) Modifier.fillMaxHeight(0.9f) else Modifier
    ) {
        Column(modifier = Modifier.padding(bottom = 32.dp)) {
            Text(
                text = "Select Type for $title",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )

            when (currentStep) {
                0 -> {
                    // Type Selection
                    listOf("Any", "Collection", "Specific Wallpaper").forEach { typeName ->
                        val isSelected = when (currentValue) {
                            is WallpaperType.Any -> typeName == "Any"
                            is WallpaperType.Collection -> typeName == "Collection"
                            is WallpaperType.Specific -> typeName == "Specific Wallpaper"
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    when (typeName) {
                                        "Any" -> onTypeSelected(WallpaperType.Any)
                                        "Collection" -> currentStep = 1
                                        "Specific Wallpaper" -> currentStep = 2
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    when (typeName) {
                                        "Any" -> onTypeSelected(WallpaperType.Any)
                                        "Collection" -> currentStep = 1
                                        "Specific Wallpaper" -> currentStep = 2
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = typeName, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }

                1 -> {
                    // Collection Picker
                    val collectionsViewModel: CollectionsViewModel =
                        viewModel(factory = CollectionsViewModelFactory(application))
                    val collections by collectionsViewModel.collections.collectAsState()

                    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 150.dp)) {
                        items(items = collections) { collection ->
                            CollectionCard(
                                collection = collection,
                                onClick = {
                                    onTypeSelected(
                                        WallpaperType.Collection(
                                            collection.id,
                                            collection.name
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .padding(12.dp)
                                    .size(150.dp)
                            )
                        }
                    }
                }

                2 -> {
                    // Wallpaper Picker
                    val wallpaperViewModel: WallpaperViewModel =
                        viewModel(factory = WallpaperViewModelFactory(application))
                    val wallpapers by wallpaperViewModel.wallpapers.collectAsState()
                    // Reusing WallpaperGrid or just simple grid
                    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 120.dp)) {
                        items(items = wallpapers) { wallpaper ->
                            // Custom card for wallpaper picking without animated scope
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .aspectRatio(0.6f)
                                    .clickable {
                                        onTypeSelected(
                                            WallpaperType.Specific(
                                                wallpaper.id,
                                                "Wallpaper ${wallpaper.id}"
                                            )
                                        )
                                    }
                            ) {
                                coil3.compose.AsyncImage(
                                    model = wallpaper.filePath, // Check what actually displays wallpaper
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
