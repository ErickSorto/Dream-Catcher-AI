package org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.ShieldMoon
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.components.DateAndTimeButtonsLayout
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.components.DreamImageSelectionRow
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.components.LucidFavoriteLayout
import org.ballistic.dreamjournalai.dream_add_edit.domain.AddEditDreamEvent
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.viewmodel.AddEditDreamState
import java.time.Clock
import java.time.LocalTime


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoPage(
    dreamBackgroundImage: MutableState<Int>,
    addEditDreamState: AddEditDreamState,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit,
) {
    CalendarDialog(
        state = addEditDreamState.calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true,
        ),
        selection = CalendarSelection.Date { date ->
            onAddEditDreamEvent(AddEditDreamEvent.ChangeDreamDate(date))
        },
    )
    ClockDialog(state = addEditDreamState.sleepTimePickerState,
        config = ClockConfig(
            //default time 12:00am
            defaultTime = Clock.systemDefaultZone().instant().atZone(Clock.systemDefaultZone().zone)
                .toLocalTime(),
            is24HourFormat = false,
        ),
        selection = ClockSelection.HoursMinutes { hour, minute ->
            onAddEditDreamEvent(AddEditDreamEvent.ChangeDreamSleepTime(LocalTime.of(hour, minute)))
        }
    )

    ClockDialog(state = addEditDreamState.wakeTimePickerState,
        config = ClockConfig(
            //default time 12:00am
            defaultTime = Clock.systemDefaultZone().instant().atZone(Clock.systemDefaultZone().zone)
                .toLocalTime(),
            is24HourFormat = false,

            ),
        selection = ClockSelection.HoursMinutes { hour, minute ->
            onAddEditDreamEvent(AddEditDreamEvent.ChangeDreamWakeTime(LocalTime.of(hour, minute)))
        }
    )

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
                DateAndTimeButtonsLayout(addEditDreamState = addEditDreamState)

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
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..10f,
            steps = 9,
            modifier = Modifier.fillMaxWidth(),
            colors = SliderDefaults.colors(
                thumbColor = colorResource(id = R.color.white),
                activeTrackColor = colorResource(id = R.color.sky_blue),
                inactiveTrackColor = colorResource(id = R.color.white).copy(alpha = 0.3f)
            )
        )
    }
}