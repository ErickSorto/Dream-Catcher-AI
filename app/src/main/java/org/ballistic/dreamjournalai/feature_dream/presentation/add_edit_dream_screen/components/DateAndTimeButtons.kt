package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.viewmodel.AddEditDreamState
import org.ballistic.dreamjournalai.R
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateAndTimeButtonsLayout(
    addEditDreamState: AddEditDreamState,
) {
    Row(
        modifier = Modifier
            .padding(0.dp, 8.dp, 0.dp, 8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.White.copy(alpha = 0.3f)),
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DateButton(
    addEditDreamState: AddEditDreamState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(0.dp, 4.dp, 0.dp, 4.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Transparent),
        colors = buttonColors(
            containerColor = Color.Transparent,
        )
    ) {
        Column(
            modifier = Modifier.background(Color.Transparent),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Date", fontSize = 12.sp, color = colorResource(id = R.color.white))
            Text(
                text = addEditDreamState.dreamInfo.dreamDate,
                fontSize = 10.sp,
                color = colorResource(id = R.color.white),
                modifier = Modifier.padding(vertical = 1.dp)
            )
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SleepTimeButton(
    addEditDreamState: AddEditDreamState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(0.dp, 4.dp, 0.dp, 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Transparent),
        colors = buttonColors(
            containerColor = Color.Transparent,
        )
    ) {
        Column(
            modifier = Modifier.background(Color.Transparent),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(text = "Sleep Time", fontSize = 12.sp, color = colorResource(id =  R.color.white))
            Text(
                text = addEditDreamState.dreamInfo.dreamSleepTime,
                fontSize = 10.sp,
                color = colorResource(id =  R.color.white)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WakeTimeButton(
    addEditDreamState: AddEditDreamState,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(0.dp, 4.dp, 0.dp, 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Transparent),
        colors = buttonColors(
            containerColor = Color.Transparent,
        )
    ) {

        Column(
            modifier = Modifier.background(Color.Transparent),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Wake Time", fontSize = 12.sp, color = colorResource(id =  R.color.white))
            Text(
                text = addEditDreamState.dreamInfo.dreamWakeTime,
                fontSize = 10.sp,
                color = colorResource(id =  R.color.white)
            )
        }
    }
}