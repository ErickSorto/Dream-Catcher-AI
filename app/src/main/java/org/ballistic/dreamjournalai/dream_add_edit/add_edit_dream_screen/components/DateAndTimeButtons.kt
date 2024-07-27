package org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.viewmodel.AddEditDreamState

@Composable
fun DateAndTimeButtonsLayout(
    addEditDreamState: AddEditDreamState,
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White.copy(alpha = 0.2f)),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DateButton(addEditDreamState = addEditDreamState,
            modifier = Modifier
                .weight(1f)
                .background(Color.Transparent),
            onClick = { addEditDreamState.calendarState.show() })
        Spacer(
            modifier = Modifier
                .width(1.dp)
                .height(32.dp)
                .background(Color.Black.copy(alpha = 0.8f))
        )
        SleepTimeButton(addEditDreamState = addEditDreamState,
            modifier = Modifier.weight(1f),
            onClick = { addEditDreamState.sleepTimePickerState.show() })
        Spacer(
            modifier = Modifier
                .width(1.dp)
                .height(32.dp)
                .background(Color.Black.copy(alpha = 0.8f))
        )
        WakeTimeButton(addEditDreamState = addEditDreamState,
            modifier = Modifier.weight(1f),
            onClick = { addEditDreamState.wakeTimePickerState.show() })
    }

}

@Composable
fun DateButton(
    addEditDreamState: AddEditDreamState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .background(Color.Transparent)
            .clickable {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.date),
            style = typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.white),
            modifier = Modifier.padding(top = 16.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = addEditDreamState.dreamInfo.dreamDate,
            style = typography.labelSmall,
            color = colorResource(id = R.color.white),
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
fun SleepTimeButton(
    addEditDreamState: AddEditDreamState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {

    Column(
        modifier = modifier
            .background(Color.Transparent)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(R.string.sleep_time),
            style = typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.white),
            modifier = Modifier.padding(top = 16.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = addEditDreamState.dreamInfo.dreamSleepTime,
            style = typography.labelSmall,
            color = colorResource(id = R.color.white),
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

@Composable
fun WakeTimeButton(
    addEditDreamState: AddEditDreamState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .background(Color.Transparent)
            .clickable {
                onClick()
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.wake_time),
            style = typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = colorResource(id = R.color.white),
            modifier = Modifier.padding(top = 16.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = addEditDreamState.dreamInfo.dreamWakeTime,
            style = typography.labelSmall,
            color = colorResource(id = R.color.white),
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}
