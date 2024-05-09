package org.ballistic.dreamjournalai.dream_tools.presentation.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.dream_store.presentation.store_screen.components.singleClick
import org.ballistic.dreamjournalai.dream_tools.presentation.DreamTools

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.DreamToolsGrid(
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigate: (Int, String) -> Unit,
    modifier: Modifier
) {
    val lastClickTime = remember { mutableLongStateOf(0L) }
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
                enabled = tool.enabled,
                onClick = singleClick(
                    lastClickTimeState = lastClickTime,
                    onClick = { onNavigate(tool.icon, tool.route) }
                ),
                modifier = Modifier.sharedElement(
                    rememberSharedContentState(key = "image/${tool.icon}"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = { _, _ ->
                        tween(500)
                    }
                ),
            )
        }
    }
}