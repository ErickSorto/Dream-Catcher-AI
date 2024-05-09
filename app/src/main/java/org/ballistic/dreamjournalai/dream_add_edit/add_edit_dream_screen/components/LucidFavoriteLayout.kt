package org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.events.AddEditDreamEvent
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.viewmodel.AddEditDreamState

@Composable
fun LucidFavoriteLayout(
    addEditDreamState: AddEditDreamState,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LucidCustomButton(
            isLucid = addEditDreamState.dreamInfo.dreamIsLucid,
            onAddEditDreamEvent = { onAddEditDreamEvent(it) }
        )
        FavoriteCustomButton(
            isFavorite = addEditDreamState.dreamInfo.dreamIsFavorite,
            onAddEditDreamEvent = { onAddEditDreamEvent(it) }
        )
    }
}

@Composable
fun LucidCustomButton(
    isLucid: Boolean,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = {
            onAddEditDreamEvent(AddEditDreamEvent.ChangeIsLucid(!isLucid))
        }

        ) {
            Icon(
                painter = rememberAsyncImagePainter(R.drawable.lighthouse_vector),
                contentDescription = "Lucid",
                modifier = Modifier.size(40.dp),
                tint = if (isLucid) colorResource(R.color.sky_blue) else colorResource(id = R.color.white).copy(alpha = 0.5f)
            )

        }
        Text(text = "Lucid", color = colorResource(id = R.color.white))
    }
}

@Composable
fun FavoriteCustomButton(
    isFavorite: Boolean,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = {
                onAddEditDreamEvent(AddEditDreamEvent.ChangeFavorite(!isFavorite))
            },
            modifier = Modifier.size(40.dp),
        ) {
            Icon(
                painter = rememberAsyncImagePainter(R.drawable.baseline_star_24),
                contentDescription = "Favorite",
                modifier = Modifier.size(40.dp),
                tint = if (isFavorite) colorResource(R.color.Yellow) else colorResource(id = R.color.white).copy(alpha = 0.5f)
            )
        }
        Text(text = "Favorite", color = colorResource(id = R.color.white))
    }
}