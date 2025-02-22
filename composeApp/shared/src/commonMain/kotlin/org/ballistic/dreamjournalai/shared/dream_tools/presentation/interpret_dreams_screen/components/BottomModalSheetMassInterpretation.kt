package org.ballistic.dreamjournalai.shared.dream_tools.presentation.interpret_dreams_screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.BrighterWhite
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.LightBlack
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.White
import org.ballistic.dreamjournalai.shared.core.components.DreamTokenLayout
import org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.components.AdTokenLayout
import org.ballistic.dreamjournalai.shared.dream_tools.presentation.interpret_dreams_screen.viewmodel.InterpretDreamsScreenState
import org.ballistic.dreamjournalai.shared.dream_tools.domain.event.InterpretDreamsToolEvent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomModalSheetMassInterpretation(
    title: String,
    dreamTokens: Int,
    interpretDreamsScreenState: InterpretDreamsScreenState,
    onEvent: (InterpretDreamsToolEvent) -> Unit,
    onAdClick: (amount: Int) -> Unit,
    onDreamTokenClick: (amount: Int) -> Unit,
    onClickOutside: () -> Unit,
    modifier: Modifier = Modifier
) {
    var state by remember { mutableStateOf(true) }
    var amount by remember { mutableIntStateOf(interpretDreamsScreenState.chosenDreams.size) }
    ModalBottomSheet(
        modifier = modifier.fillMaxWidth(),
        onDismissRequest = {
            onClickOutside()
        },
        content = {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp, 0.dp, 16.dp, 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ){
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = BrighterWhite,
                        modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 8.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    DreamTokenLayout(
                        totalDreamTokens = dreamTokens,
                        modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 8.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.selectableGroup()
                ) {
                    RadioButton(
                        selected = state,
                        onClick = {
                            onEvent(InterpretDreamsToolEvent.UpdateModel("gpt-4o-mini"))
                            state = true
                            amount = interpretDreamsScreenState.chosenDreams.size
                        },
                        modifier = Modifier.semantics { contentDescription = "Localized Description" }
                    )

                    //standard ai
                    Text(text = "Standard AI", color = White)

                    Spacer(modifier = Modifier.weight(1f))

                    RadioButton(
                        selected = !state,
                        onClick = {
                            onEvent(InterpretDreamsToolEvent.UpdateModel("gpt-4o"))
                            state = false
                            amount = interpretDreamsScreenState.chosenDreams.size * 2
                        },
                        modifier = Modifier.semantics { contentDescription = "Localized Description" }
                    )
                    //Advanced AI
                    Text(
                        text = "Advanced AI",
                        color = White,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                AdTokenLayout(
                    isAdButtonVisible = amount <= 3,
                    onAdClick = { amount ->
                        onAdClick(amount) // Use the lambda correctly
                    },
                    onDreamTokenClick = { amount ->
                        onDreamTokenClick(amount) // Use the lambda correctly
                    },
                    amount = amount
                )
            }
        },
        containerColor = LightBlack,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
    )
}