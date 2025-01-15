package org.ballistic.dreamjournalai.shared.core.util

import androidx.compose.runtime.Composable

@Composable
expect fun BackHandler(isEnabled: Boolean, onBack: ()-> Unit)