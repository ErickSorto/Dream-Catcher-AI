package org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.viewmodel.AddEditDreamState
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.LightBlack
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.White
import org.ballistic.dreamjournalai.shared.core.components.DreamTokenLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageGenerationPopUp(
    addEditDreamState: AddEditDreamState,
    onDreamTokenClick: (amount: Int) -> Unit,
    onAdClick: (amount: Int) -> Unit,
    onClickOutside: () -> Unit,
    modifier: Modifier = Modifier
) {
    var state by remember { mutableStateOf(true) }
    var amount by remember { mutableIntStateOf(2) }

    ModalBottomSheet(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        onDismissRequest = {
            onClickOutside()
        },
        content = {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .animateContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Dream Painter",
                        style = MaterialTheme.typography.headlineMedium,
                        color = White,
                        modifier = Modifier.padding(8.dp)
                    )
                    DreamTokenLayout(
                        totalDreamTokens = addEditDreamState.dreamTokens
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.selectableGroup()
                ) {
                    RadioButton(
                        selected = state,
                        onClick = {
                            state = true
                            amount = 2
                        },
                        modifier = Modifier.semantics {
                            contentDescription = "Localized Description"
                        }
                    )
                    Text(
                        text = "High Quality",
                        color = White,
                        modifier = Modifier.padding(end = 8.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    RadioButton(
                        selected = !state,
                        onClick = {
                            state = false
                            amount = 1
                        },
                        modifier = Modifier.semantics {
                            contentDescription = "Localized Description"
                        }
                    )

                    Text(text = "Low Quality", color = White)
                }
                AdTokenLayout(
                    isAdButtonVisible = amount <= 1,
                    onAdClick = { amount ->
                        onAdClick(amount)
                    },
                    onDreamTokenClick = { amount ->
                        onDreamTokenClick(amount)
                    },
                    amount = amount
                )
            }
        },
        containerColor = LightBlack
    )
}
