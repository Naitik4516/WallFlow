package com.ns.wallflow.ui.components

import androidx.compose.material3.Card
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment

@Composable
fun SelectableGridItem(
    isSelected: Boolean,
    isSelectionMode: Boolean,
    onCheckedChange: () -> Unit,
    item: @Composable () -> Unit,
) {
    // Animate the scale slightly when selected for a polished feel
    val scale by animateFloatAsState(targetValue = if (isSelected) 0.95f else 1f, label = "scale")

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .graphicsLayer(scaleX = scale, scaleY = scale),
//            .combinedClickable(
//                onClick = onClick,
//                onLongClick = onLongClick
//            )

        border = BorderStroke(
            width = if (isSelected) 2.dp else 0.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            item()

            // Dark overlay + Checkbox when selected or in selection mode
            if (isSelectionMode) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            else Color.Transparent
                        )
                )

                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onCheckedChange() }, // Redirect to the same click logic
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                )
            }
        }
    }
}