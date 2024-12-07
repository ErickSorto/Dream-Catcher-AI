package org.ballistic.dreamjournalai.dream_add_edit.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.dream_add_edit.presentation.viewmodel.AddEditDreamState
import org.ballistic.dreamjournalai.dream_add_edit.domain.AddEditDreamEvent

@Composable
fun LucidFavoriteLayout(
    addEditDreamState: AddEditDreamState,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp, 8.dp, 0.dp, 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            LucidCustomButton(
                isLucid = addEditDreamState.dreamInfo.dreamIsLucid,
                onAddEditDreamEvent = onAddEditDreamEvent
            )
            RecurringCustomButton(
                isRecurring = addEditDreamState.dreamInfo.dreamIsRecurring,
                onAddEditDreamEvent = onAddEditDreamEvent
            )
            FalseAwakeningCustomButton(
                    isFalseAwakening = addEditDreamState.dreamInfo.dreamIsFalseAwakening,
            onAddEditDreamEvent = onAddEditDreamEvent
            )
            NightmareCustomButton(
                isNightmare = addEditDreamState.dreamInfo.dreamIsNightmare,
                onAddEditDreamEvent = onAddEditDreamEvent
            )
            FavoriteCustomButton(
                isFavorite = addEditDreamState.dreamInfo.dreamIsFavorite,
                onAddEditDreamEvent = onAddEditDreamEvent
            )
        }
    }
}


@Composable
fun LucidCustomButton(
    isLucid: Boolean,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .size(68.dp)
            .clickable {
                onAddEditDreamEvent(AddEditDreamEvent.ChangeIsLucid(!isLucid))
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Icon(
            painter = painterResource(R.drawable.lighthouse_vector),
            contentDescription = "Lucid",
            modifier = Modifier.size(36.dp),
            tint = if (isLucid) colorResource(R.color.sky_blue) else colorResource(id = R.color.white).copy(
                alpha = 0.5f
            )
        )

        Text(
            text = "Lucid",
            color = colorResource(id = R.color.white),
            style = typography.labelSmall
        )
    }
}

@Composable
fun FavoriteCustomButton(
    isFavorite: Boolean,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .size(68.dp)
            .clickable {
                onAddEditDreamEvent(AddEditDreamEvent.ChangeFavorite(!isFavorite))
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))
            Icon(
                painter = painterResource(R.drawable.baseline_star_24),
                contentDescription = "Favorite",
                modifier = Modifier.size(36.dp),
                tint = if (isFavorite) colorResource(R.color.Yellow) else colorResource(id = R.color.white).copy(
                    alpha = 0.5f
                )
            )
        Text(
            text = "Favorite", color = colorResource(id = R.color.white),
            style = typography.labelSmall
        )
    }
}

@Composable
fun NightmareCustomButton(
    isNightmare: Boolean,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .size(68.dp)
            .clickable {
                onAddEditDreamEvent(AddEditDreamEvent.ChangeNightmare(!isNightmare))
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))
            Icon(
                painter = painterResource(R.drawable.nightmare_icon),
                contentDescription = "Nightmare",
                modifier = Modifier.size(36.dp),
                tint = if (isNightmare) colorResource(R.color.RedOrange) else colorResource(id = R.color.white).copy(
                    alpha = 0.5f
                )
            )
        Text(
            text = "Nightmare",
            color = colorResource(id = R.color.white),
            style = typography.labelSmall
        )
    }
}

@Composable
fun FalseAwakeningCustomButton(
    isFalseAwakening: Boolean,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .size(68.dp)
            .clickable {
                onAddEditDreamEvent(AddEditDreamEvent.ChangeFalseAwakening(!isFalseAwakening))
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))
            Icon(
                painter = painterResource(R.drawable.false_awakening_icon),
                contentDescription = "False",
                modifier = Modifier.size(36.dp),
                tint = if (isFalseAwakening) colorResource(R.color.purple) else colorResource(id = R.color.white).copy(
                    alpha = 0.5f
                )
            )
        Text(
            text = "False",
            color = colorResource(id = R.color.white),
            style = typography.labelSmall
        )
    }
}

@Composable
fun RecurringCustomButton(
    isRecurring: Boolean,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .clip(MaterialTheme.shapes.medium)
            .size(68.dp)
            .clickable {
                onAddEditDreamEvent(AddEditDreamEvent.ChangeRecurrence(!isRecurring))
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(8.dp))
            Icon(
                painter = painterResource(R.drawable.baseline_cached_24),
                contentDescription = "Recurring",
                modifier = Modifier.size(36.dp),
                tint = if (isRecurring) colorResource(R.color.green) else colorResource(id = R.color.white).copy(
                    alpha = 0.5f
                )
            )
        Text(
            text = "Recurring",
            color = colorResource(id = R.color.white),
            style = typography.labelSmall
        )
    }
}