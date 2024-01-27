package org.ballistic.dreamjournalai.dream_tools.presentation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.dream_tools.presentation.DreamTools

@Composable
fun DreamToolsGrid(
    onNavigate: (String) -> Unit,
    modifier: Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(5.dp),
        modifier = modifier
    ) {
        items(DreamTools.entries.toTypedArray()) { tool ->
            DreamToolItem(
                title = tool.title,
                icon = tool.icon,
                description = tool.description,
                onClick = {
                    onNavigate(tool.route)
                }
            )
        }
    }
}