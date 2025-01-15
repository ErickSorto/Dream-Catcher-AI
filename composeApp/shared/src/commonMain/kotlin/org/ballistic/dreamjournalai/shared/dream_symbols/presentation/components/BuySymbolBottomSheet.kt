package org.ballistic.dreamjournalai.shared.dream_symbols.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.LightBlack
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.White
import org.ballistic.dreamjournalai.shared.core.components.DreamTokenLayout
import org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.components.AdTokenLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuySymbolBottomSheet(
    title: String,
    token: Int,
    onAdClick: () -> Unit,
    onDreamTokenClick: () -> Unit,
    onClickOutside: () -> Unit,
    modifier: Modifier = Modifier,
    amount: Int
) {
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
                    .padding(8.dp, 0.dp, 8.dp, 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ){
                    Text(
                        text = title,
                        style = if (title.length > 20) {
                            MaterialTheme.typography.titleMedium
                        } else {
                            MaterialTheme.typography.headlineSmall
                        },
                        color = White,
                        modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 8.dp)
                    )
                    DreamTokenLayout(
                        totalDreamTokens = token,
                    )
                }
                AdTokenLayout(
                    onAdClick = { _ ->
                        onAdClick()
                    },
                    onDreamTokenClick = { _ ->
                        onDreamTokenClick()
                    },
                    amount = amount
                )
            }
        },
        containerColor = LightBlack
    )
}
