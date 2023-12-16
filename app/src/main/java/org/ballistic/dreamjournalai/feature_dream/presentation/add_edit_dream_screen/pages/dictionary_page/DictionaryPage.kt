package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.dictionary_page

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import org.ballistic.dreamjournalai.dream_dictionary.presentation.DictionaryEvent
import org.ballistic.dreamjournalai.dream_dictionary.presentation.components.DictionaryWordItem
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.AddEditDreamEvent
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.viewmodel.AddEditDreamState
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel.MainScreenViewModelState

//@RequiresApi(Build.VERSION_CODES.O)
//@OptIn(ExperimentalPagerApi::class)
//@Composable
//fun DictionaryPage(
//    pagerState: PagerState,
//    addEditDreamState: AddEditDreamState,
//    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit,
//) {
//    val processedWords = dictionaryScreenState.filteredWords.map { wordItem ->
//        wordItem.copy(
//            isUnlocked = wordItem.isUnlocked || dictionaryScreenState.unlockedWords.contains(wordItem.word),
//            cost = if (dictionaryScreenState.unlockedWords.contains(wordItem.word)) 0 else wordItem.cost
//        )
//    }
//
//    LazyColumn(
//        state = listState,
//        modifier = Modifier.weight(1f),
//        contentPadding = PaddingValues(bottom = 32.dp)
//    ) {
//        items(processedWords) { wordItem ->
//            Log.d("DictionaryScreen", "Displaying word: ${wordItem.word}")
//            DictionaryWordItem(
//                wordItem = wordItem,
//                onWordClick = { onEvent(DictionaryEvent.ClickWord(wordItem)) }
//            )
//        }
//    }
//}