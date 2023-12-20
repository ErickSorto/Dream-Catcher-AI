package org.ballistic.dreamjournalai.dream_dictionary.presentation

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.dream_dictionary.presentation.components.BuyDictionaryWordDrawer
import org.ballistic.dreamjournalai.dream_dictionary.presentation.components.DictionaryScreenTopBar
import org.ballistic.dreamjournalai.dream_dictionary.presentation.components.DictionaryWordDrawer
import org.ballistic.dreamjournalai.dream_dictionary.presentation.components.DictionaryWordItem
import org.ballistic.dreamjournalai.dream_dictionary.presentation.viewmodel.DictionaryScreenState
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.MainScreenEvent
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel.MainScreenViewModelState

@Composable
fun DictionaryScreen(
    dictionaryScreenState: DictionaryScreenState,
    mainScreenViewModelState: MainScreenViewModelState,
    onEvent: (DictionaryEvent) -> Unit = {},
    onMainEvent: (MainScreenEvent) -> Unit = {},
) {
    val alphabet = remember { ('A'..'Z').toList() }
    val listState = rememberLazyListState()
    var selectedHeader by remember { mutableStateOf('A') }
    val screenWidth = remember { mutableStateOf(0) } // to store screen width
    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    val scope = rememberCoroutineScope()

    // create vibrator effect with the constant EFFECT_CLICK

    val vibrationEffect1: VibrationEffect =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
        } else {
            Log.e("TAG", "Cannot vibrate device..")
            TODO("VERSION.SDK_INT < O")
        }

    LaunchedEffect(Unit) {
        Log.d("DictionaryScreen", "LaunchedEffect triggered")
        onEvent(DictionaryEvent.LoadWords)
        onEvent(DictionaryEvent.GetUnlockedWords)
        onEvent(DictionaryEvent.FilterByLetter('A'))
        onMainEvent(MainScreenEvent.SetSearchingState(false))
    }


    Scaffold(
        snackbarHost = {
            dictionaryScreenState.snackBarHostState.value
        },
        topBar = {
            DictionaryScreenTopBar(
                mainScreenViewModelState = mainScreenViewModelState,
            )
        },
        containerColor = Color.Transparent
    ) {
        if (!dictionaryScreenState.isClickedWordUnlocked && dictionaryScreenState.bottomSheetState.value) {
            BuyDictionaryWordDrawer(
                title = dictionaryScreenState.clickedWord.word,
                onAdClick = {
                    scope.launch {
                        onEvent(
                            DictionaryEvent.ClickBuyWord(
                                dictionaryWord = dictionaryScreenState.clickedWord,
                                isAd = true,
                                activity = context as Activity,
                            )
                        )
                    }
                },
                onDreamTokenClick = {
                    onEvent(
                        DictionaryEvent.ClickBuyWord(
                            dictionaryWord = dictionaryScreenState.clickedWord,
                            isAd = false,
                            activity = context as Activity,
                        )
                    )
                },
                onClickOutside = {
                    dictionaryScreenState.bottomSheetState.value = false
                },
                token = dictionaryScreenState.dreamTokens.value,
                amount = dictionaryScreenState.clickedWord.cost
            )
        } else if (dictionaryScreenState.isClickedWordUnlocked && dictionaryScreenState.bottomSheetState.value) {
            DictionaryWordDrawer(
                title = dictionaryScreenState.clickedWord.word,
                definition = dictionaryScreenState.clickedWord.definition,
                onClickOutside = {
                    dictionaryScreenState.bottomSheetState.value = false
                },
            )
        }
        Column(
            modifier = Modifier
                .padding(it)
                .navigationBarsPadding()
                .fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = colorResource(id = R.color.dark_blue).copy(alpha = 0.5f))
                    .onGloballyPositioned { layoutCoordinates ->
                        screenWidth.value = layoutCoordinates.size.width // Store the width
                    }
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { change, _ ->
                            val positionX = change.position.x
                            val letterWidth = screenWidth.value / alphabet.size.toFloat()
                            val index =
                                (positionX / letterWidth).coerceIn(
                                    0f,
                                    (alphabet.size - 1).toFloat()
                                )
                            val letter = alphabet[index.toInt()]
                            if (selectedHeader != letter) {
                                selectedHeader = letter
                                onEvent(DictionaryEvent.FilterByLetter(letter))
                                vibrator.cancel()
                                vibrator.vibrate(vibrationEffect1)
                            }
                        }
                    }
                    .padding(horizontal = 2.dp)
            ) {
                alphabet.forEach { letter ->
                    Text(
                        text = letter.toString(),
                        modifier = Modifier
                            .animateContentSize { _, _ -> }
                            .weight(1f)
                            .clickable {
                                vibrator.vibrate(vibrationEffect1)
                                selectedHeader = letter
                                onEvent(DictionaryEvent.FilterByLetter(letter))
                            }
                            .scale(if (letter == selectedHeader) 1.5f else 1f) // Slightly scale up the selected letter
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.Center,
                        fontSize = 10.sp,
                        fontWeight = if (letter == selectedHeader) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (letter == selectedHeader) Color.White else Color.White.copy(
                            alpha = 0.5f
                        )
                    )
                }
            }
            val processedWords = dictionaryScreenState.filteredWords.map { wordItem ->
                wordItem.copy(
                    isUnlocked = wordItem.isUnlocked || dictionaryScreenState.unlockedWords.contains(wordItem.word),
                    cost = if (dictionaryScreenState.unlockedWords.contains(wordItem.word)) 0 else wordItem.cost
                )
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(processedWords) { wordItem ->
                    Log.d("DictionaryScreen", "Displaying word: ${wordItem.word}")
                    DictionaryWordItem(
                        wordItem = wordItem,
                        onWordClick = { onEvent(DictionaryEvent.ClickWord(wordItem)) }
                    )
                }
            }
        }
    }
}
