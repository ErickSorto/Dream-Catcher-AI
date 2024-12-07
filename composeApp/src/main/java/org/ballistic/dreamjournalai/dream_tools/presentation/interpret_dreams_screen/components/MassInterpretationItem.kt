package org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen.components

import android.os.Vibrator
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.util.VibrationUtil.triggerVibration
import org.ballistic.dreamjournalai.dream_tools.domain.model.MassInterpretation
import org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen.viewmodel.InterpretDreamsScreenState
import org.ballistic.dreamjournalai.dream_tools.domain.event.InterpretDreamsToolEvent

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun MassInterpretationItem(
    interpretDreamsScreenState: InterpretDreamsScreenState,
    onEvent: (InterpretDreamsToolEvent) -> Unit,
    pagerState: PagerState,
    scope: CoroutineScope,
    vibrator: Vibrator,
    massInterpretation: MassInterpretation
) {

    var isLongPressTriggered by remember { mutableStateOf(false) }
    Card(
        colors = CardDefaults.cardColors(
            containerColor = colorResource(id = R.color.light_black).copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, 16.dp, 16.dp, 0.dp)
            .combinedClickable(
                onClick = {
                    if (!isLongPressTriggered) {
                        onEvent(InterpretDreamsToolEvent.ChooseMassInterpretation(massInterpretation))
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    }
                },
                onLongClick = {
                    onEvent(InterpretDreamsToolEvent.ChooseMassInterpretation(massInterpretation))
                    isLongPressTriggered = true
                    scope.launch {
                        if (isLongPressTriggered) {
                            triggerVibration(vibrator)
                            onEvent(InterpretDreamsToolEvent.ToggleBottomDeleteCancelSheetState(true))
                        }
                        isLongPressTriggered = false // Reset after handling
                    }
                }
            )
    ) {


        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Interpretation: ${massInterpretation.listOfDreamIDs.size} dreams",
                style = typography.titleSmall.copy(fontSize = 15.sp),
                color = Color.White,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(start = 16.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color = colorResource(id = R.color.light_black).copy(alpha = 0.8f))
            ) {
                Text(
                    text = massInterpretation.model,
                    style = typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = massInterpretation.interpretation,
            style = typography.bodyMedium,
            color = Color.White,
            maxLines = 5,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 0.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(16.dp, 0.dp, 16.dp, 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (dreamUID in massInterpretation.listOfDreamIDs) {
                if (interpretDreamsScreenState.dreams.find { it.id == dreamUID } != null) {
                    val dream = interpretDreamsScreenState.dreams.find { it.id == dreamUID }!!
                    SmallDreamItem(
                        dream = dream,
                        imageSize = 25.dp,
                        vibrator = vibrator,
                    ) {

                    }
                } else {
                    SmallDreamItem(
                        isDeleted = true,
                        imageSize = 25.dp,
                        vibrator = vibrator,
                    ) {

                    }
                }
            }
        }
    }
}