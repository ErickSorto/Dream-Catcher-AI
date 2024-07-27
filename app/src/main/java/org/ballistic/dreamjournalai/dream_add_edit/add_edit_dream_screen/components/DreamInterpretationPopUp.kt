package org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.components

import androidx.compose.animation.animateContentSize
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.components.DreamTokenLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DreamInterpretationPopUp(
    title: String,
    dreamTokens: Int,
    onAdClick: (amount: Int) -> Unit,
    onDreamTokenClick: (amount: Int) -> Unit,
    onClickOutside: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var state by remember { mutableStateOf(true) }
    var amount by remember { mutableIntStateOf(0) }
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
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp, 0.dp, 16.dp, 16.dp)
                        .animateContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium,
                            color = colorResource(id = R.color.brighter_white),
                            modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 8.dp)
                        )
                        DreamTokenLayout(
                            totalDreamTokens = dreamTokens,
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
                                amount = 0
                                      },
                            modifier = Modifier.semantics { contentDescription = "Localized Description" }
                        )

                        //standard ai
                        Text(text = stringResource(R.string.standard_ai), color = colorResource(id = R.color.white))

                        Spacer(modifier = Modifier.weight(1f))

                        RadioButton(
                            selected = !state,
                            onClick = {
                                state = false
                                amount = 1
                                      },
                            modifier = Modifier.semantics { contentDescription = "Localized Description" }
                        )
                        //Advanced AI
                        Text(
                            text = stringResource(R.string.advanced_ai),
                            color = colorResource(id = R.color.white),
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    AdTokenLayout(
                        isAdButtonVisible = amount <= 1 && amount != 0,
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
        containerColor = colorResource(id = R.color.light_black)
    )
}

