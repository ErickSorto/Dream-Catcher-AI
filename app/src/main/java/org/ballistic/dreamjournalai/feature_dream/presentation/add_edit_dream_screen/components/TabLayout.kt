package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.AddEditDreamEvent
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.AIPage.AIPage
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.DreamPage
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.InfoPage
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.dictionary_page.WordPage
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.viewmodel.AddEditDreamState
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel.MainScreenViewModelState


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalPagerApi::class)
@Composable
@UiComposable
fun TabLayout(
    dreamBackgroundImage: MutableState<Int>,
    mainScreenViewModelState: MainScreenViewModelState,
    addEditDreamState: AddEditDreamState,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit,
) {
    val pages = listOf("Dream", "AI", "Words", "Info")
    val pagerState = rememberPagerState()
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current


    //keyboard control
    val keyboardController = LocalSoftwareKeyboardController.current


    //create tab layout with 3 tabs, one for dream title and content, one for dream information and one for dream artificial intelligence
    TabRow(
        modifier = Modifier,
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPositions ->
            SecondaryIndicator(// custom indicator
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                color = colorResource(id = R.color.white)
            )
        },
        contentColor = colorResource(id = R.color.white),
        containerColor = colorResource(id = R.color.dark_blue).copy(alpha = 0.5f),
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
                    focusManager.clearFocus()
                    scope.launch { pagerState.animateScrollToPage(index) }
                }
            )
        }


    }
    HorizontalPager(
        count = pages.size,
        state = pagerState,
    ) { page ->
        when (page) {
            0 -> {
                DreamPage(
                    pagerState,
                    addEditDreamState = addEditDreamState,
                    onAddEditDreamEvent = onAddEditDreamEvent
                )
            }

            1 -> {
                AIPage(
                    pagerState,
                    addEditDreamState = addEditDreamState,
                    onAddEditDreamEvent = onAddEditDreamEvent,
                    mainScreenViewModelState = mainScreenViewModelState
                )
            }

            2 -> {
                WordPage(
                    addEditDreamState = addEditDreamState,
                    onAddEditDreamEvent = onAddEditDreamEvent,
                    mainScreenViewModelState = mainScreenViewModelState
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