package com.ns.wallflow.ui.components
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.ns.wallflow.ui.icons.Close


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectionTopAppBar(
    isSelectionMode: Boolean,
    selectedCount: Int,
    onClearSelection: () -> Unit,
    actions: @Composable () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = if (isSelectionMode) "$selectedCount Selected" else "My Gallery",
                fontSize = if (isSelectionMode) 18.sp else 22.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        navigationIcon = {
            if (isSelectionMode) {
                IconButton(onClick = onClearSelection) {
                    Icon(Close, contentDescription = "Clear selection")
                }
            }
        },
        actions = { actions() }
    )
}

//@Preview
//@Composable
//fun SelectionTopAppBarPreview() {
//    SelectionTopAppBar(
//        isSelectionMode = true,
//        selectedCount = 3,
//        onClearSelection = {},
//    )
//}