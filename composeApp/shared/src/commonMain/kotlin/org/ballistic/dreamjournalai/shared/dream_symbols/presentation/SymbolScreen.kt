package org.ballistic.dreamjournalai.shared.dream_symbols.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lexilabs.basic.ads.DependsOnGoogleMobileAds
import app.lexilabs.basic.ads.RewardedAd
import coil3.compose.LocalPlatformContext
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.shared.core.components.dynamicBottomNavigationPadding
import org.ballistic.dreamjournalai.shared.dream_main.domain.MainScreenEvent
import org.ballistic.dreamjournalai.shared.dream_main.presentation.viewmodel.MainScreenViewModelState
import org.ballistic.dreamjournalai.shared.dream_symbols.domain.SymbolEvent
import org.ballistic.dreamjournalai.shared.dream_symbols.presentation.components.BuySymbolBottomSheet
import org.ballistic.dreamjournalai.shared.dream_symbols.presentation.components.DictionaryWordDrawer
import org.ballistic.dreamjournalai.shared.dream_symbols.presentation.components.DictionaryWordItem
import org.ballistic.dreamjournalai.shared.dream_symbols.presentation.components.SymbolScreenTopBar
import org.ballistic.dreamjournalai.shared.dream_symbols.presentation.viewmodel.SymbolScreenState
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.DarkBlue

@OptIn(DependsOnGoogleMobileAds::class)
@Composable
fun SymbolScreen(
    symbolScreenState: SymbolScreenState,
    mainScreenViewModelState: MainScreenViewModelState,
    bottomPaddingValue: Dp,
    searchTextFieldState: TextFieldState,
    onEvent: (SymbolEvent) -> Unit = {},
    onMainEvent: (MainScreenEvent) -> Unit = {},
) {
    val alphabet = remember { ('A'..'Z').toList() }
    var selectedHeader by remember { mutableStateOf('A') }
    val screenWidth = remember { mutableIntStateOf(0) } // to store screen width
    val scope = rememberCoroutineScope()
    val tokens = symbolScreenState.dreamTokens

    LaunchedEffect(Unit) {
        onEvent(SymbolEvent.GetDreamTokens)
        onEvent(SymbolEvent.LoadWords)
        onEvent(SymbolEvent.GetUnlockedWords)
        onEvent(SymbolEvent.FilterByLetter('A'))
        onMainEvent(MainScreenEvent.SetSearchingState(false))
    }


    Scaffold(
        snackbarHost = {
            SnackbarHost(symbolScreenState.snackBarHostState.value)
        },
        topBar = {
            SymbolScreenTopBar(
                mainScreenViewModelState = mainScreenViewModelState,
                symbolScreenState = symbolScreenState,
                searchedTextFieldState = searchTextFieldState,
                onDictionaryEvent = onEvent
            )
        },
        containerColor = Color.Transparent,
    ) {
        if (!symbolScreenState.isClickedWordUnlocked && symbolScreenState.bottomSheetState.value) {
            BuySymbolBottomSheet(
                title = symbolScreenState.clickedSymbol.word,
                onAdClick = {
                    scope.launch {
                        onEvent(
                            SymbolEvent.AdSymbolToggle(true)
                        )
                    }
                },
                onDreamTokenClick = {
                    onEvent(
                        SymbolEvent.ClickBuySymbol(
                            dictionaryWord = symbolScreenState.clickedSymbol,
                            isAd = false
                        )
                    )
                },
                onClickOutside = {
                    symbolScreenState.bottomSheetState.value = false
                },
                token = tokens,
                amount = symbolScreenState.clickedSymbol.cost
            )
        } else if (symbolScreenState.isClickedWordUnlocked && symbolScreenState.bottomSheetState.value) {
            DictionaryWordDrawer(
                title = symbolScreenState.clickedSymbol.word,
                definition = symbolScreenState.clickedSymbol.definition,
                onClickOutside = {
                    symbolScreenState.bottomSheetState.value = false
                },
            )
        }
        if(symbolScreenState.isAdSymbol){
            RewardedAd(
                activity = LocalPlatformContext.current,
                adUnitId = "ca-app-pub-8710979310678386/8178296701",
                onRewardEarned = {
                    onEvent(
                        SymbolEvent.ClickBuySymbol(
                            dictionaryWord = symbolScreenState.clickedSymbol,
                            isAd = true
                        )
                    )
                    onEvent(SymbolEvent.AdSymbolToggle(false))
                },
                onDismissed = {
                    onEvent(SymbolEvent.AdSymbolToggle(false))
                }
            )
        }
        Column(
            modifier = Modifier
                .padding(top = it.calculateTopPadding(), bottom = bottomPaddingValue)
                .dynamicBottomNavigationPadding()
                .fillMaxSize()
        ) {
            val processedWords = symbolScreenState.filteredWordsByLetter.map { wordItem ->
                wordItem.copy(
                    isUnlocked = wordItem.isUnlocked || symbolScreenState.unlockedWords.contains(
                        wordItem.word
                    ),
                    cost = if (symbolScreenState.unlockedWords.contains(wordItem.word)) 0 else wordItem.cost
                )
            }
            if (symbolScreenState.isSearching) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 32.dp, top = 8.dp)
                ) {
                    items(symbolScreenState.filteredSearchedWords) { wordItem ->
                        DictionaryWordItem(
                            wordItem = wordItem,
                            onWordClick = { onEvent(SymbolEvent.ClickWord(wordItem)) }
                        )
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = DarkBlue.copy(alpha = 0.5f))
                        .onGloballyPositioned { layoutCoordinates ->
                            screenWidth.intValue = layoutCoordinates.size.width // Store the width
                        }
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { change, _ ->
                                val positionX = change.position.x
                                val letterWidth = screenWidth.intValue / alphabet.size.toFloat()
                                val index =
                                    (positionX / letterWidth).coerceIn(
                                        0f,
                                        (alphabet.size - 1).toFloat()
                                    )
                                val letter = alphabet[index.toInt()]
                                if (selectedHeader != letter) {
                                    selectedHeader = letter
                                    onEvent(SymbolEvent.FilterByLetter(letter))
                                    onEvent(SymbolEvent.CancelVibration)
                                    onEvent(SymbolEvent.TriggerVibration)
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
                                    onEvent(SymbolEvent.TriggerVibration)
                                    selectedHeader = letter
                                    onEvent(SymbolEvent.FilterByLetter(letter))
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
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 32.dp, top = 8.dp)
                ) {
                    items(processedWords) { wordItem ->
                        DictionaryWordItem(
                            wordItem = wordItem,
                            onWordClick = { onEvent(SymbolEvent.ClickWord(wordItem)) }
                        )
                    }
                }
            }
        }
    }
}
