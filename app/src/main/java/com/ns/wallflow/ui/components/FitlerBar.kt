package com.ns.wallflow.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.foundation.shape.CircleShape


@Composable
fun FilterBar(
    timeFilters: List<String>,
    brightnessFilters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String, String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        item {
            FilterChip(
                label = { Text("All") },
                onClick = { onFilterSelected("All", "all") },
                selected = selectedFilter == "All",
                shape = CircleShape
            )
        }
        items(timeFilters) { filter ->
            FilterChip(
                label = { Text(filter) },
                onClick = { onFilterSelected(filter, "time") },
                selected = selectedFilter == filter,
                shape = CircleShape
            )
        }
        items(brightnessFilters) { filter ->
            FilterChip(
                label = { Text(filter) },
                onClick = { onFilterSelected(filter, "brightness") },
                selected = selectedFilter == filter,
                shape = CircleShape
            )
        }
    }
}
