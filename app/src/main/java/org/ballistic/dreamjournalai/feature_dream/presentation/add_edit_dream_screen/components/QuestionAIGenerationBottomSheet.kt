package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.components.DreamTokenLayout
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.AddEditDreamEvent
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.viewmodel.AddEditDreamState
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel.MainScreenViewModelState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionAIGenerationBottomSheet(
    addEditDreamState: AddEditDreamState,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit,
    onDreamTokenClick: (amount: Int) -> Unit,
    onAdClick: (amount: Int) -> Unit,
    onClickOutside: () -> Unit,
    modifier: Modifier = Modifier
) {
    var state by remember { mutableStateOf(true) }
    var amount by remember { mutableIntStateOf(1) }
    ModalBottomSheet(
        onDismissRequest = onClickOutside,
        windowInsets = WindowInsets.ime,
        content = {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp, 8.dp, 16.dp, 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Dream Question",
                        style = MaterialTheme.typography.headlineMedium,
                        color = colorResource(id = R.color.white),
                        modifier = Modifier.padding(8.dp)
                    )
                    DreamTokenLayout(
                        totalDreamTokens = addEditDreamState.dreamTokens
                            .collectAsStateWithLifecycle().value,
                    )
                }
                OutlinedTextField(
                    value = addEditDreamState.dreamQuestionAIAnswer.question,
                    onValueChange = {
                        onAddEditDreamEvent(AddEditDreamEvent.ChangeQuestionOfDream(it))
                    },
                    label = {
                        Text(
                            text = "Ask your Question",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(8.dp, 8.dp, 8.dp, 16.dp),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    singleLine = false,
                    maxLines = 2,
                    // Define the colors for the text field
                    colors = // Define the colors as per your theme
                    OutlinedTextFieldDefaults.colors(

                    )
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.selectableGroup()
                ) {
                    RadioButton(
                        selected = state,
                        onClick = {
                            state = true
                            amount = 1
                        },
                        modifier = Modifier.semantics { contentDescription = "Localized Description" }
                    )

                   Text(
                        text = "Standard AI",
                        color = colorResource(id = R.color.white)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    RadioButton(
                        selected = !state,
                        onClick = {
                            state = false
                            amount = 2
                        },
                        modifier = Modifier.semantics { contentDescription = "Localized Description" }
                    )

                    Text(
                        text = "Advanced AI",
                        color = colorResource(id = R.color.white),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                // AdTokenLayout for managing Ads and Tokens
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
        containerColor = colorResource(id = R.color.dark_purple)
    )
}
