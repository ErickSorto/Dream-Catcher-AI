package org.ballistic.dreamjournalai.dream_add_edit.presentation.pages

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.dream_add_edit.domain.AddEditDreamEvent
import org.ballistic.dreamjournalai.dream_add_edit.presentation.components.DateAndTimeButtonsLayout
import org.ballistic.dreamjournalai.dream_add_edit.presentation.components.DreamImageSelectionRow
import org.ballistic.dreamjournalai.dream_add_edit.presentation.components.LucidFavoriteLayout
import org.ballistic.dreamjournalai.dream_add_edit.presentation.viewmodel.AddEditDreamState
import org.ballistic.dreamjournalai.dream_notifications.presentation.components.DialWithDialogExample

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoPage(
    dreamBackgroundImage: MutableState<Int>,
    addEditDreamState: AddEditDreamState,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit,
) {
    // Existing CalendarDialog usage remains unchanged
    if (addEditDreamState.calendarDialogState) {
        DatePickerModal(
            onDateSelected = { selectedDate ->
                selectedDate?.let {
                    onAddEditDreamEvent(AddEditDreamEvent.ChangeDreamDate(it))
                }
                // After selection (or null if canceled), close the dialog
                onAddEditDreamEvent(AddEditDreamEvent.ToggleCalendarDialog(show = false))
            },
            onDismiss = {
                // If user cancels, close the dialog
                onAddEditDreamEvent(AddEditDreamEvent.ToggleCalendarDialog(show = false))
            }
        )
    }
    // Sleep time picker
    if (addEditDreamState.sleepTimePickerDialogState) {
        DialWithDialogExample(
            onConfirm = { timePickerState ->
                onAddEditDreamEvent(
                    AddEditDreamEvent.ChangeDreamSleepTime(
                        LocalTime(timePickerState.hour, timePickerState.minute)
                    )
                )
                // Close sleep dialog
                onAddEditDreamEvent(AddEditDreamEvent.ToggleSleepTimePickerDialog(show = false))
            },
            onDismiss = {
                // Close sleep dialog without action
                onAddEditDreamEvent(AddEditDreamEvent.ToggleSleepTimePickerDialog(show = false))
            }
        )
    }

    // Wake time picker
    if (addEditDreamState.wakeTimePickerDialogState) {
        DialWithDialogExample(
            onConfirm = { timePickerState ->
                onAddEditDreamEvent(
                    AddEditDreamEvent.ChangeDreamWakeTime(
                        LocalTime(timePickerState.hour, timePickerState.minute)
                    )
                )
                // Close wake dialog
                onAddEditDreamEvent(AddEditDreamEvent.ToggleWakeTimePickerDialog(show = false))
            },
            onDismiss = {
                // Close wake dialog without action
                onAddEditDreamEvent(AddEditDreamEvent.ToggleWakeTimePickerDialog(show = false))
            }
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp, start = 14.dp, end = 14.dp, top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(color = colorResource(id = R.color.light_black).copy(alpha = 0.7f))
        ) {
            Column {
                Text(
                    text = "Dream Background",
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp, 16.dp, 16.dp, 0.dp), //bold
                    style = typography.titleMedium.copy(color = colorResource(id = R.color.white))
                        .copy(
                            fontWeight = FontWeight.Normal
                        ),
                    textAlign = TextAlign.Center,
                )

                DreamImageSelectionRow(
                    dreamBackgroundImage = dreamBackgroundImage,
                    onAddEditDreamEvent = onAddEditDreamEvent
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(color = colorResource(id = R.color.light_black).copy(alpha = 0.7f))
        ) {
            //row for isLucid
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LucidFavoriteLayout(
                    addEditDreamState = addEditDreamState,
                    onAddEditDreamEvent = onAddEditDreamEvent
                )
                DateAndTimeButtonsLayout(
                    addEditDreamState = addEditDreamState,
                    onDateClick = { onAddEditDreamEvent(AddEditDreamEvent.ToggleCalendarDialog(show = true)) },
                    onSleepTimeClick = { onAddEditDreamEvent(AddEditDreamEvent.ToggleSleepTimePickerDialog(show = true)) },
                    onWakeTimeClick = { onAddEditDreamEvent(AddEditDreamEvent.ToggleWakeTimePickerDialog(show = true)) }
                    )

                Spacer(modifier = Modifier.height(24.dp))


                // Slider for lucidity
                SliderWithLabel(
                    label = "Lucidity",
                    value = addEditDreamState.dreamInfo.dreamLucidity,
                    onValueChange = { onAddEditDreamEvent(AddEditDreamEvent.ChangeLucidity(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Slider for vividness
                SliderWithLabel(
                    label = "Vividness",
                    value = addEditDreamState.dreamInfo.dreamVividness,
                    onValueChange = { onAddEditDreamEvent(AddEditDreamEvent.ChangeVividness(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(20.dp))

                // Slider for mood
                SliderWithLabel(
                    label = "Mood",
                    value = addEditDreamState.dreamInfo.dreamEmotion,
                    onValueChange = { onAddEditDreamEvent(AddEditDreamEvent.ChangeMood(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun SliderWithLabel(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedValue by animateFloatAsState(targetValue = value.toFloat(), label = "")

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$label: $value",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            style = typography.bodyLarge.copy(color = colorResource(id = R.color.white))
        )
        Slider(
            value = animatedValue,
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..5f,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = colorResource(id = R.color.white),
                activeTrackColor = colorResource(id = R.color.sky_blue),
                inactiveTrackColor = colorResource(id = R.color.white).copy(alpha = 0.3f)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (LocalDate?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                val selectedMillis = datePickerState.selectedDateMillis
                val selectedDate = selectedMillis?.let {
                    // Use UTC to avoid the one-day offset issue.
                    Instant.fromEpochMilliseconds(it)
                        .toLocalDateTime(TimeZone.UTC)
                        .date
                }
                onDateSelected(selectedDate)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}