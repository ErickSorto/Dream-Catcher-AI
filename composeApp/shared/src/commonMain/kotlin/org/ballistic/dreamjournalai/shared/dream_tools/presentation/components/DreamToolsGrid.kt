package org.ballistic.dreamjournalai.shared.dream_tools.presentation.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.shared.dream_store.presentation.store_screen.components.singleClick
import org.ballistic.dreamjournalai.shared.navigation.DreamTools
import org.ballistic.dreamjournalai.shared.navigation.ToolRoute

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.DreamToolsGrid(
    animatedVisibilityScope: AnimatedVisibilityScope,
    onNavigate: (ToolRoute) -> Unit,
    modifier: Modifier
) {
    val lastClickTime = remember { mutableLongStateOf(0L) }
    val dreamToolsList = DreamTools.entries // the enum’s values

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(5.dp),
        modifier = modifier
    ) {
        // Use 'items()' so the grid knows how many items there are
        items(dreamToolsList.size) { index ->
            val tool = dreamToolsList[index]
            DreamToolItem(
                title = tool.title,
                icon = tool.route.image,
                description = tool.description,
                enabled = tool.enabled,
                onClick = singleClick(
                    lastClickTimeState = lastClickTime,
                    onClick = { onNavigate(tool.route) }
                ),
                modifier = Modifier.sharedElement(
                    rememberSharedContentState(key = "image/${tool.route.image}"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = { _, _ ->
                        tween(500)
                    }
                ),
            )
        }
    }
}
