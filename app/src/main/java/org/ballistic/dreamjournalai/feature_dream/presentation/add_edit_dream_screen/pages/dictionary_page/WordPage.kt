package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.pages.dictionary_page

import android.app.Activity
import android.util.Log
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.components.TypewriterText
import org.ballistic.dreamjournalai.dream_dictionary.presentation.components.BuyDictionaryWordDrawer
import org.ballistic.dreamjournalai.dream_dictionary.presentation.components.DictionaryWordDrawer
import org.ballistic.dreamjournalai.dream_dictionary.presentation.components.DictionaryWordItem
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.AddEditDreamEvent
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.ArcRotationAnimation
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.viewmodel.AddEditDreamState


@Composable
fun WordPage(
    addEditDreamState: AddEditDreamState,
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit
) {
    val dreamTokens = addEditDreamState.dreamTokens.collectAsStateWithLifecycle().value
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (addEditDreamState.dreamContentChanged) {
            onAddEditDreamEvent(AddEditDreamEvent.FilterDreamWordInDictionary)
        }
    }
    val listState = rememberLazyListState()
    val processedWords = addEditDreamState.dreamFilteredDictionaryWords.map { wordItem ->
        wordItem.copy(
            isUnlocked = wordItem.isUnlocked || addEditDreamState.unlockedWords.contains(wordItem.word),
            cost = if (addEditDreamState.unlockedWords.contains(wordItem.word)) 0 else wordItem.cost
        )
    }

    if (addEditDreamState.dreamFilteredDictionaryWords.isEmpty() && !addEditDreamState.isDreamFilterLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .background(
                        color = colorResource(id = R.color.dark_blue).copy(alpha = 0.7f),
                        shape = RoundedCornerShape(16.dp)
                    ),
            ) {
                TypewriterText(
                    text = "No words found in your dream from the dictionary. " +
                            "Try adding more words to your dream!",
                    modifier = Modifier.padding(16.dp),
                    textAlign = TextAlign.Center,
                    animationDuration = 2000,
                )
            }
        }
    }

    if (addEditDreamState.isDreamFilterLoading) {
        Log.d("DictionaryScreen", "Loading words")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(16.dp, 0.dp, 16.dp, 16.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            ArcRotationAnimation(
                infiniteTransition = infiniteTransition,
            )
        }
    } else {
        if (!addEditDreamState.isClickedWordUnlocked && addEditDreamState.bottomSheetState.value) {
            BuyDictionaryWordDrawer(
                title = addEditDreamState.clickedWord.word,
                onAdClick = {
                    scope.launch {
                        onAddEditDreamEvent(
                            AddEditDreamEvent.ClickBuyWord(
                                dictionaryWord = addEditDreamState.clickedWord,
                                isAd = true,
                                activity = context as Activity,
                            )
                        )
                    }
                },
                onDreamTokenClick = {
                    onAddEditDreamEvent(
                        AddEditDreamEvent.ClickBuyWord(
                            dictionaryWord = addEditDreamState.clickedWord,
                            isAd = false,
                            activity = context as Activity,
                        )
                    )
                },
                onClickOutside = {
                    addEditDreamState.bottomSheetState.value = false
                },
                token = dreamTokens,
                amount = addEditDreamState.clickedWord.cost
            )
        } else if (addEditDreamState.isClickedWordUnlocked && addEditDreamState.bottomSheetState.value) {
            DictionaryWordDrawer(
                title = addEditDreamState.clickedWord.word,
                definition = addEditDreamState.clickedWord.definition,
                onClickOutside = {
                    addEditDreamState.bottomSheetState.value = false
                },
            )
        }
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(processedWords) { wordItem ->
                Log.d("DictionaryScreen", "Displaying word: ${wordItem.word}")
                DictionaryWordItem(
                    wordItem = wordItem,
                    onWordClick = { onAddEditDreamEvent(AddEditDreamEvent.ClickWord(wordItem)) }
                )
            }
        }
    }
}