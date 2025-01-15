package org.ballistic.dreamjournalai.shared.dream_symbols.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.BrighterWhite
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.LightBlack
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.White
import org.ballistic.dreamjournalai.shared.core.components.TypewriterText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryWordDrawer(
    title: String,
    definition: String,
    onClickOutside: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        onDismissRequest = onClickOutside,
        content = {
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp, 0.dp, 16.dp, 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = White.copy(alpha = 0.2f),
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = BrighterWhite,
                        modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 8.dp).fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )

                    TypewriterText(
                        text = definition,
                        style = MaterialTheme.typography.bodyLarge,
                        color = BrighterWhite,
                        modifier = Modifier.padding(8.dp).fillMaxWidth(),
                        textAlign = TextAlign.Start
                    )
                }
            }
        },
        containerColor = LightBlack
    )
}
