package org.ballistic.dreamjournalai.dream_symbols.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.components.DreamTokenLayout
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.components.AdTokenLayout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuySymbolBottomSheet(
    title: String,
    token: Int,
    onAdClick: () -> Unit,
    onDreamTokenClick: () -> Unit,
    onClickOutside: () -> Unit,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = WindowInsets.displayCutout,
    amount: Int
) {
    val bottomPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    ModalBottomSheet(
        windowInsets = windowInsets,
        onDismissRequest = {
            onClickOutside()
        },
        content = {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = bottomPadding)
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
                        color = colorResource(id = R.color.white),
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
        containerColor = colorResource(id = R.color.light_black)
    )
}
