package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.AddEditDreamEvent
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.AddEditDreamViewModel

@Composable
fun LucidFavoriteLayout(
    viewModel: AddEditDreamViewModel = hiltViewModel(),
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LucidCustomButton(
            isLucid = viewModel.dreamUiState.value.dreamInfo.dreamIsLucid,
            viewModel = viewModel
        )
        FavoriteCustomButton(
            isFavorite = viewModel.dreamUiState.value.dreamInfo.dreamIsFavorite,
            viewModel = viewModel
        )
    }
}

@Composable
fun LucidCustomButton(
    isLucid: Boolean,
    viewModel: AddEditDreamViewModel = hiltViewModel(),
) {
    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = { viewModel.onEvent(AddEditDreamEvent.ChangeIsLucid(!isLucid))}) {
            Icon(
                painter = rememberAsyncImagePainter(R.drawable.lighthouse_vector),
                contentDescription = "Lucid",
                modifier = Modifier.size(40.dp),
                tint = if (isLucid) colorResource(R.color.sky_blue) else Color.Black
            )

        }
        Text(text = "Lucid")
    }
}

@Composable
fun FavoriteCustomButton(
    isFavorite: Boolean,
    viewModel: AddEditDreamViewModel = hiltViewModel(),
) {
    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = { viewModel.onEvent(AddEditDreamEvent.ChangeFavorite(!isFavorite)) },
            modifier = Modifier.size(40.dp),
        ) {
            Icon(
                painter = rememberAsyncImagePainter(R.drawable.baseline_star_24),
                contentDescription = "Favorite",
                modifier = Modifier.size(40.dp),
                tint = if (isFavorite) colorResource(R.color.Yellow) else Color.Black
            )
        }
        Text(text = "Favorite")
    }
}