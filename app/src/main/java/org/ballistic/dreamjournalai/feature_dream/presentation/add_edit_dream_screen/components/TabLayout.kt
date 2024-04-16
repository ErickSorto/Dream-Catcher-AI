package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.PrimaryIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.AddEditDreamEvent
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.AIPage.AIPage
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.DreamPage
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.InfoPage
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.dictionary_page.WordPage
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.viewmodel.AddEditDreamState


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
@UiComposable
fun TabLayout(
    dreamBackgroundImage: MutableState<Int>,
    dreamTitleState: TextFieldState,
    dreamContentState: TextFieldState,
    addEditDreamState: AddEditDreamState,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit,
    keyboardController: SoftwareKeyboardController?
) {
    val pages = listOf("Dream", "AI", "Words", "Info")
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current

    PrimaryTabRow(
        modifier = Modifier,
        selectedTabIndex = pagerState.currentPage,
        indicator = {
            PrimaryIndicator(
                color = colorResource(id = R.color.white),
                modifier = Modifier.tabIndicatorOffset(pagerState.currentPage)
            )
        },
        contentColor = colorResource(id = R.color.white),
        containerColor = colorResource(id = R.color.light_black).copy(alpha = 0.7f),
    ) {
        pages.forEachIndexed { index, page ->
            Tab(
                text = {
                    Text(
                        text = page,
                        style = typography.labelLarge
                    )
                },
                selected = pagerState.currentPage == index,
                onClick = {
                    keyboardController?.hide()
                    focusManager.clearFocus(true)
                    scope.launch { pagerState.animateScrollToPage(index) }
                }
            )
        }


    }
    HorizontalPager(
        state = pagerState,
    ) { page ->
        when (page) {
            0 -> {
                DreamPage(
                    pagerState,
                    titleTextFieldState = dreamTitleState,
                    contentTextFieldState = dreamContentState,
                )
            }

            1 -> {
                AIPage(
                    pagerState,
                    addEditDreamState = addEditDreamState,
                    onAddEditDreamEvent = onAddEditDreamEvent,
                    textFieldState = dreamContentState
                )
            }

            2 -> {
                WordPage(
                    addEditDreamState = addEditDreamState,
                    onAddEditDreamEvent = onAddEditDreamEvent
                )
            }

            3 -> {
                InfoPage(
                    dreamBackgroundImage,
                    addEditDreamState = addEditDreamState,
                    onAddEditDreamEvent = onAddEditDreamEvent
                )
            }
        }
    }
}