package org.ballistic.dreamjournalai.dream_add_edit.presentation.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageSize
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.google.android.gms.ads.rewarded.RewardItem
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.ballistic.dreamjournalai.core.Resource
import org.ballistic.dreamjournalai.core.util.OpenAIApiKeyUtil.getOpenAISecretKey
import org.ballistic.dreamjournalai.core.util.formatLocalDate
import org.ballistic.dreamjournalai.core.util.formatLocalTime
import org.ballistic.dreamjournalai.dream_ad.domain.AdCallback
import org.ballistic.dreamjournalai.dream_ad.domain.AdManagerRepository
import org.ballistic.dreamjournalai.dream_add_edit.domain.AddEditDreamEvent
import org.ballistic.dreamjournalai.dream_add_edit.domain.AddEditDreamEvent.*
import org.ballistic.dreamjournalai.dream_authentication.domain.repository.AuthRepository
import org.ballistic.dreamjournalai.dream_journal_list.domain.model.Dream
import org.ballistic.dreamjournalai.dream_journal_list.domain.model.InvalidDreamException
import org.ballistic.dreamjournalai.dream_journal_list.domain.use_case.DreamUseCases
import org.ballistic.dreamjournalai.dream_symbols.presentation.viewmodel.DictionaryWord
import java.io.IOException //TODO: Use different exception
import java.util.Locale //TODO: Expect/actual for this
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// Get current date
private val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
private val currentDate = now.date

// Static sleep and wake times
private val sleepTime = LocalTime(23, 0) // 11 PM
private val wakeTime = LocalTime(7, 0)   // 7 A

class AddEditDreamViewModel(
    private val dreamUseCases: DreamUseCases,
    private val adManagerRepository: AdManagerRepository,
    private val authRepository: AuthRepository,
    private val application: Application,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _addEditDreamState = MutableStateFlow(
        AddEditDreamState(
            authRepository = authRepository
        )
    )
    val addEditDreamState: StateFlow<AddEditDreamState> = _addEditDreamState.asStateFlow()

    private val _titleTextFieldState = MutableStateFlow(TextFieldState())

    val titleTextFieldState: StateFlow<TextFieldState> = _titleTextFieldState.asStateFlow()

    private val _contentTextFieldState = MutableStateFlow(TextFieldState())

    val contentTextFieldState: StateFlow<TextFieldState> = _contentTextFieldState.asStateFlow()
    val flow = Unit;
    private suspend fun listenForContentChanges() {
        snapshotFlow {
            contentTextFieldState.value.text
        }.collect { text ->
            if (text.isNotEmpty()) {
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamHasChanged = true,
                    dreamContentChanged = true
                )
            }
        }

        snapshotFlow {
            titleTextFieldState.value.text
        }.collect { text ->
            if (text.isNotEmpty()) {
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamHasChanged = true,
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        onEvent(OnCleared)
    }

    init {
        savedStateHandle.get<String>("dreamID")?.let { dreamId ->
            if (dreamId.isNotEmpty()) {
                viewModelScope.launch {
                    _addEditDreamState.value = addEditDreamState.value.copy(isLoading = true)
                    when (val resource = dreamUseCases.getDream(dreamId)) {
                        is Resource.Success -> {
                            resource.data?.let { dream ->
                                _titleTextFieldState.value = TextFieldState(
                                    initialText = resource.data.title
                                )
                                _contentTextFieldState.value = TextFieldState(
                                    initialText = resource.data.content
                                )
                                _addEditDreamState.value = AddEditDreamState(
                                    dreamInfo = DreamInfo(
                                        dreamId = dream.id,
                                        dreamUID = dream.uid,
                                        dreamBackgroundImage = dream.backgroundImage,
                                        dreamIsLucid = dream.isLucid,
                                        dreamIsFavorite = dream.isFavorite,
                                        dreamIsNightmare = dream.isNightmare,
                                        dreamIsRecurring = dream.isRecurring,
                                        dreamIsFalseAwakening = dream.falseAwakening,
                                        dreamSleepTime = dream.sleepTime,
                                        dreamWakeTime = dream.wakeTime,
                                        dreamDate = dream.date,
                                        dreamTimeOfDay = dream.timeOfDay,
                                        dreamLucidity = dream.lucidityRating,
                                        dreamVividness = dream.vividnessRating,
                                        dreamEmotion = dream.moodRating
                                    ),
                                    dreamAIExplanation = DreamAIExplanation(
                                        response = dream.AIResponse,
                                    ),
                                    dreamAIImage = DreamAIImage(
                                        response = dream.generatedImage,
                                    ),
                                    dreamAIQuestionAnswer = DreamQuestionAIAnswer(
                                        response = dream.dreamAIQuestionAnswer,
                                        question = dream.dreamQuestion
                                    ),
                                    dreamAIAdvice = DreamAIAdvice(
                                        response = dream.dreamAIAdvice
                                    ),
                                    dreamAIMoodAnalyser = DreamMoodAIAnalyser(
                                        response = dream.dreamAIMood
                                    ),
                                    dreamAIStory = DreamStoryGeneration(
                                        response = dream.dreamAIStory
                                    ),
                                    dreamGeneratedDetails = DreamAIGeneratedDetails(
                                        response = dream.generatedDetails,
                                    ),
                                    isLoading = false,
                                    authRepository = authRepository
                                )
                            }
                            onEvent(
                                StartListening
                            )
                            onEvent(
                                ToggleDreamHasChanged(false)
                            )
                            onEvent(GetUnlockedWords)
                            onEvent(LoadWords)
                            onEvent(GetDreamTokens)
                        }

                        is Resource.Error -> {
                            // handle error
                        }

                        is Resource.Loading -> {
                            // handle loading
                        }
                    }
                }
            } else {
                onEvent(
                    StartListening
                )
                onEvent(GetUnlockedWords)
                onEvent(LoadWords)
                onEvent(GetDreamTokens)
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun onEvent(event: AddEditDreamEvent) {
        when (event) {
            is ChangeDreamBackgroundImage -> {
                onEvent(ToggleDreamHasChanged(true))
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamBackgroundImage = event.dreamBackGroundImage
                    )
                )
            }

            is ClickGenerateAIResponse -> {
                onEvent(ToggleDreamHasChanged(true))
                getAIResponse(command = "Please interpret the following dream: ${
                    contentTextFieldState.value.text
                } ",
                    isAd = event.isAd,
                    cost = event.cost,
                    activity = event.activity,
                    updateLoadingState = { isLoading ->
                        _addEditDreamState.value = addEditDreamState.value.copy(
                            dreamAIExplanation = addEditDreamState.value.dreamAIExplanation.copy(
                                isLoading = isLoading
                            )
                        )
                    },
                    updateResponseState = { response ->
                        _addEditDreamState.value = addEditDreamState.value.copy(
                            dreamAIExplanation = addEditDreamState.value.dreamAIExplanation.copy(
                                response = response
                            )
                        )
                    })
            }


            is ClickGenerateAIAdvice -> {
                onEvent(ToggleDreamHasChanged(true))
                getAIResponse(command = "Please give advice that can be obtained or for this dream: ${
                    contentTextFieldState.value.text
                } ",
                    isAd = event.isAd,
                    cost = event.cost,
                    activity = event.activity,
                    updateLoadingState = { isLoading ->
                        _addEditDreamState.value = addEditDreamState.value.copy(
                            dreamAIAdvice = addEditDreamState.value.dreamAIAdvice.copy(isLoading = isLoading)
                        )
                    },
                    updateResponseState = { advice ->
                        _addEditDreamState.value = addEditDreamState.value.copy(
                            dreamAIAdvice = addEditDreamState.value.dreamAIAdvice.copy(response = advice)
                        )
                    })
            }

            is ClickGenerateMood -> {
                onEvent(ToggleDreamHasChanged(true))
                getAIResponse(command = "Please describe the mood of this dream: ${
                    contentTextFieldState.value.text
                }",
                    isAd = event.isAd,
                    cost = event.cost,
                    activity = event.activity,
                    updateLoadingState = { isLoading ->
                        _addEditDreamState.value = addEditDreamState.value.copy(
                            dreamAIMoodAnalyser = addEditDreamState.value.dreamAIMoodAnalyser.copy(
                                isLoading = isLoading
                            )
                        )
                    },
                    updateResponseState = { mood ->
                        _addEditDreamState.value = addEditDreamState.value.copy(
                            dreamAIMoodAnalyser = addEditDreamState.value.dreamAIMoodAnalyser.copy(
                                response = mood
                            )
                        )
                    })
            }

            is ClickGenerateStory -> {
                onEvent(ToggleDreamHasChanged(true))
                getAIResponse(command = "Please generate a very short story based on this dream: ${
                    contentTextFieldState.value.text
                } ",
                    isAd = event.isAd,
                    cost = event.cost,
                    activity = event.activity,
                    updateLoadingState = { isLoading ->
                        _addEditDreamState.value = addEditDreamState.value.copy(
                            dreamAIStory = addEditDreamState.value.dreamAIStory.copy(
                                isLoading = isLoading
                            )
                        )
                    },
                    updateResponseState = { story ->
                        _addEditDreamState.value = addEditDreamState.value.copy(
                            dreamAIStory = addEditDreamState.value.dreamAIStory.copy(
                                response = story
                            )
                        )
                    })
            }

            is ClickGenerateFromQuestion -> {
                onEvent(ToggleDreamHasChanged(true))
                getAIResponse(command = "Please answer the following question: ${
                    addEditDreamState.value.dreamAIQuestionAnswer.question
                }" + "as it relates to this dream: ${
                    contentTextFieldState.value.text
                }",
                    isAd = event.isAd,
                    cost = event.cost,
                    activity = event.activity,
                    updateLoadingState = { isLoading ->
                        _addEditDreamState.value = addEditDreamState.value.copy(
                            dreamAIQuestionAnswer = addEditDreamState.value.dreamAIQuestionAnswer.copy(
                                isLoading = isLoading
                            )
                        )
                    },
                    updateResponseState = { answer ->
                        _addEditDreamState.value = addEditDreamState.value.copy(
                            dreamAIQuestionAnswer = addEditDreamState.value.dreamAIQuestionAnswer.copy(
                                response = answer
                            )
                        )
                    })
            }

            is ClickGenerateAIImage -> {
                _addEditDreamState.value = addEditDreamState.value.copy(isDreamExitOff = true)
                viewModelScope.launch {
                    if (!event.isAd) {
                        onEvent(ToggleDreamHasChanged(true))
                        getAIDetailsResponse(event.cost).await()
                        getOpenAIImageResponse(event.cost).await()
                        onEvent(SaveDream {
                            onEvent(ToggleDreamHasChanged(false))
                        })
                    } else {
                        runAd(event.activity, onRewardedAd = {
                            onEvent(ToggleDreamHasChanged(true))
                            viewModelScope.launch {
                                getAIDetailsResponse(0).await()
                                getOpenAIImageResponse(0).await()
                                onEvent(SaveDream {
                                    onEvent(ToggleDreamHasChanged(false))
                                })
                            }
                        }, onAdFailed = {
                            _addEditDreamState.value =
                                addEditDreamState.value.copy(isDreamExitOff = false)
                            viewModelScope.launch {
                                addEditDreamState.value.snackBarHostState.value.showSnackbar(
                                    "Ad failed to load",
                                    duration = SnackbarDuration.Short,
                                    actionLabel = "Dismiss"
                                )
                            }
                        })
                    }
                }
            }

            is ChangeLucidity -> {
                onEvent(ToggleDreamHasChanged(true))
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamLucidity = event.lucidity
                    )
                )
            }

            is ChangeVividness -> {
                onEvent(ToggleDreamHasChanged(true))
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamVividness = event.vividness
                    )
                )
            }

            is ChangeMood -> {
                onEvent(ToggleDreamHasChanged(true))
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamEmotion = event.mood
                    )
                )
            }

            is ChangeNightmare -> {
                onEvent(ToggleDreamHasChanged(true))
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamIsNightmare = event.boolean
                    )
                )
            }

            is ChangeRecurrence -> {
                onEvent(ToggleDreamHasChanged(true))
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamIsRecurring = event.boolean
                    )
                )
            }

            is ChangeIsLucid -> {
                onEvent(ToggleDreamHasChanged(true))
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamIsLucid = event.boolean
                    )
                )
            }

            is ChangeFavorite -> {
                onEvent(ToggleDreamHasChanged(true))
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamIsFavorite = event.boolean
                    )
                )
            }

            is ChangeFalseAwakening -> {
                onEvent(ToggleDreamHasChanged(true))
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamIsFalseAwakening = event.boolean
                    )
                )
            }

            is ChangeTimeOfDay -> {
                onEvent(ToggleDreamHasChanged(true))
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamTimeOfDay = event.timeOfDay
                    )
                )
            }

            is ClickGenerateFromDescription -> {
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamAIImage = DreamAIImage(
                        isLoading = true
                    )
                )
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamAIExplanation = DreamAIExplanation(
                        isLoading = true
                    )
                )
            }

            is ChangeDetailsOfDream -> {
                onEvent(ToggleDreamHasChanged(true))
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamGeneratedDetails = DreamAIGeneratedDetails(
                        response = event.value
                    )
                )
            }

            is DeleteDream -> {
                viewModelScope.launch {
                    dreamUseCases.deleteDream(SavedStateHandle()["dreamId"]!!)
                }
            }

            is ChangeDreamDate -> {
                onEvent(ToggleDreamHasChanged(true))
                val formattedDate = formatLocalDate(event.value)
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamDate = formattedDate
                    )
                )
            }

            is ChangeDreamWakeTime -> {
                onEvent(ToggleDreamHasChanged(true))
                val formattedWakeTime = formatLocalTime(event.value)
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamWakeTime = formattedWakeTime
                    )
                )
            }

            is ChangeDreamSleepTime -> {
                onEvent(ToggleDreamHasChanged(true))
                val formattedSleepTime = formatLocalTime(event.value)
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamSleepTime = formattedSleepTime
                    )
                )
            }

            is ChangeQuestionOfDream -> {
                onEvent(ToggleDreamHasChanged(true))
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamAIQuestionAnswer = DreamQuestionAIAnswer(
                        question = event.value
                    )
                )
            }

            is ClickWord -> {
                _addEditDreamState.value = addEditDreamState.value.copy(
                    bottomSheetState = true,
                    isClickedWordUnlocked = event.word.cost == 0,
                    clickedWord = event.word
                )
            }

            is FilterDreamWordInDictionary -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _addEditDreamState.value = addEditDreamState.value.copy(
                        dreamFilteredDictionaryWords = dictionaryWordsInDreamFilterList(),
                        dreamContentChanged = false
                    )
                }
            }

            is LoadWords -> {
                viewModelScope.launch {
                    loadWords()
                }
            }

            is StartListening -> {
                viewModelScope.launch {
                    listenForContentChanges()
                }
            }

            is GetUnlockedWords -> {
                viewModelScope.launch {
                    authRepository.getUnlockedWords().collect { result ->
                        when (result) {
                            is Resource.Loading -> {
                                // Handle loading state if needed
                            }

                            is Resource.Success -> {
                                Log.d("AddEditDreamViewModel", "Unlocked words: ${result.data}")
                                _addEditDreamState.update { state ->
                                    state.copy(
                                        unlockedWords = result.data?.toMutableList()
                                            ?: mutableListOf()
                                    )
                                }
                            }

                            is Resource.Error -> {
                                _addEditDreamState.value.snackBarHostState.value.showSnackbar(
                                    message = "Couldn't get unlocked words :(",
                                    actionLabel = "Dismiss"
                                )
                            }
                        }
                    }
                }
            }

            is ClickBuyWord -> {
                _addEditDreamState.value = addEditDreamState.value.copy(
                    isDreamExitOff = true
                )
                viewModelScope.launch {
                    handleUnlockWord(event)
                }
            }

            is ToggleDreamImageGenerationPopUpState -> {
                _addEditDreamState.update {
                    it.copy(
                        dreamImageGenerationPopUpState = event.value
                    )
                }
            }

            is ToggleDreamInterpretationPopUpState -> {
                _addEditDreamState.update {
                    it.copy(
                        dreamInterpretationPopUpState = event.value
                    )
                }
            }

            is ToggleDreamAdvicePopUpState -> {
                _addEditDreamState.update {
                    it.copy(
                        dreamAdvicePopUpState = event.value
                    )
                }
            }

            is ToggleDreamQuestionPopUpState -> {
                _addEditDreamState.update {
                    it.copy(
                        dreamQuestionPopUpState = event.value
                    )
                }
            }

            is ToggleDreamStoryPopUpState -> {
                _addEditDreamState.update {
                    it.copy(
                        dreamStoryPopupState = event.value
                    )
                }
            }

            is ToggleDreamMoodPopUpState -> {
                _addEditDreamState.update {
                    it.copy(
                        dreamMoodPopupState = event.value
                    )
                }
            }

            is ToggleDialogState -> {
                _addEditDreamState.update {
                    it.copy(dialogState = event.value)
                }
            }

            is ToggleBottomSheetState -> {
                _addEditDreamState.update {
                    it.copy(bottomSheetState = event.value)
                }
            }

            is SaveDream -> {
                Log.d("AddEditDreamViewModel", "Saving dream")
                _addEditDreamState.update {
                    it.copy(dreamIsSavingLoading = true)
                }

                viewModelScope.launch {
                    if (titleTextFieldState.value.text.isBlank() && contentTextFieldState.value.text.isNotEmpty()) {
                        makeAIRequest(
                            command = "Please generate a title for this dream with only 1 to 4 words no quotes. Don't include the word dream: ${contentTextFieldState.value.text}",
                            cost = 0,
                            updateResponseState = { title ->
                                _titleTextFieldState.value = TextFieldState(initialText = title)
                            },
                            updateLoadingState = {}
                        )
                    }

                    if (_addEditDreamState.value.dreamInfo.dreamId.isNullOrEmpty()) {
                        _addEditDreamState.update {
                            it.copy(
                                dreamInfo = it.dreamInfo.copy(
                                    dreamId = Uuid.random().toString()
                                )
                            )
                        }
                    }

                    try {
                        val dreamToSave = Dream(
                            id = addEditDreamState.value.dreamInfo.dreamId,
                            uid = addEditDreamState.value.dreamInfo.dreamUID,
                            title = titleTextFieldState.value.text.toString(),
                            content = contentTextFieldState.value.text.toString(),
                            timestamp = System.currentTimeMillis(),
                            date = addEditDreamState.value.dreamInfo.dreamDate,
                            sleepTime = addEditDreamState.value.dreamInfo.dreamSleepTime,
                            wakeTime = addEditDreamState.value.dreamInfo.dreamWakeTime,
                            AIResponse = addEditDreamState.value.dreamAIExplanation.response,
                            isFavorite = addEditDreamState.value.dreamInfo.dreamIsFavorite,
                            isLucid = addEditDreamState.value.dreamInfo.dreamIsLucid,
                            isNightmare = addEditDreamState.value.dreamInfo.dreamIsNightmare,
                            isRecurring = addEditDreamState.value.dreamInfo.dreamIsRecurring,
                            falseAwakening = addEditDreamState.value.dreamInfo.dreamIsFalseAwakening,
                            lucidityRating = addEditDreamState.value.dreamInfo.dreamLucidity,
                            moodRating = addEditDreamState.value.dreamInfo.dreamEmotion,
                            vividnessRating = addEditDreamState.value.dreamInfo.dreamVividness,
                            timeOfDay = addEditDreamState.value.dreamInfo.dreamTimeOfDay,
                            backgroundImage = addEditDreamState.value.dreamInfo.dreamBackgroundImage,
                            generatedImage = addEditDreamState.value.dreamAIImage.response,
                            generatedDetails = addEditDreamState.value.dreamGeneratedDetails.response,
                            dreamQuestion = addEditDreamState.value.dreamAIQuestionAnswer.question,
                            dreamAIQuestionAnswer = addEditDreamState.value.dreamAIQuestionAnswer.response,
                            dreamAIStory = addEditDreamState.value.dreamAIStory.response,
                            dreamAIAdvice = addEditDreamState.value.dreamAIAdvice.response,
                            dreamAIMood = addEditDreamState.value.dreamAIMoodAnalyser.response
                        )

                        dreamUseCases.addDream(dreamToSave)

                        _addEditDreamState.update {
                            it.copy(
                                dreamIsSavingLoading = false,
                                saveSuccess = true
                            )
                        }
                        event.onSaveSuccess()
                        Log.d("AddEditDreamViewModel", "Dream saved successfully")

                    } catch (e: InvalidDreamException) {
                        _addEditDreamState.update {
                            it.copy(dreamIsSavingLoading = false, saveSuccess = false)
                        }
                        addEditDreamState.value.snackBarHostState.value.showSnackbar(
                            e.message ?: "Couldn't save dream :(",
                            actionLabel = "Dismiss",
                            duration = SnackbarDuration.Long
                        )
                    }
                }
            }

            is ToggleDreamHasChanged -> {
                _addEditDreamState.update {
                    it.copy(
                        dreamHasChanged = event.value
                    )
                }
            }

            is FlagDreamContent -> {
                viewModelScope.launch{
                    val result = dreamUseCases.flagDream(dreamID = addEditDreamState.value.dreamInfo.dreamId,
                        imagePath = addEditDreamState.value.dreamAIImage.response)

                    when(result){
                        is Resource.Success -> {
                            addEditDreamState.value.snackBarHostState.value.showSnackbar(
                                message = "Dream flagged successfully",
                                actionLabel = "Dismiss"
                            )
                        }
                        is Resource.Error -> {
                            addEditDreamState.value.snackBarHostState.value.showSnackbar(
                                message = "Couldn't flag dream :(",
                                actionLabel = "Dismiss"
                            )
                        }

                        is Resource.Loading -> {
                            // Handle loading state if needed
                        }
                    }
                }
            }

            is GetDreamTokens -> {
                viewModelScope.launch {
                    authRepository.addDreamTokensFlowListener().collect { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                Log.d("AddEditDreamViewModel", "Dream tokens: ${resource.data}")
                                _addEditDreamState.update {
                                    it.copy(
                                        dreamTokens = resource.data?.toInt() ?: 0
                                    )
                                }
                            }

                            is Resource.Error -> {
                                // Handle error
                            }

                            is Resource.Loading -> {
                                // Handle loading state if needed
                            }
                        }
                    }
                }
            }
            is OnCleared -> {

            }

            is ToggleSleepTimePickerDialog -> {
                _addEditDreamState.update {
                    it.copy(
                        sleepTimePickerDialogState = event.show
                    )
                }
            }
            is ToggleWakeTimePickerDialog -> {
                _addEditDreamState.update {
                    it.copy(
                        wakeTimePickerDialogState = event.show
                    )
                }
            }
            is ToggleCalendarDialog -> {
                _addEditDreamState.update {
                    it.copy(
                        calendarDialogState = event.show
                    )
                }
            }
        }
    }

    private fun handleUnlockWord(event: ClickBuyWord) {
        if (event.isAd) {
            runAd(activity = event.activity, onRewardedAd = {
                unlockWordWithAd(event.dictionaryWord)
                _addEditDreamState.value = addEditDreamState.value.copy(
                    isDreamExitOff = false
                )
            }, onAdFailed = {
                Log.d("DictionaryScreen", "Ad failed")
                _addEditDreamState.value = addEditDreamState.value.copy(
                    isDreamExitOff = false
                )
            })
        } else {
            unlockWordWithTokens(event.dictionaryWord)
            _addEditDreamState.value = addEditDreamState.value.copy(
                isDreamExitOff = false
            )
        }
    }

    private fun unlockWordWithAd(dictionaryWord: DictionaryWord) {
        viewModelScope.launch {
            Log.d("DictionaryScreen", "Unlocking word with ad")
            processUnlockWordResult(
                result = authRepository.unlockWord(dictionaryWord.word, 0),
                dictionaryWord = dictionaryWord
            )
        }
    }

    private fun unlockWordWithTokens(dictionaryWord: DictionaryWord) {
        Log.d("DictionaryScreen", "Unlocking word with dream tokens")
        viewModelScope.launch {
            processUnlockWordResult(
                result = authRepository.unlockWord(dictionaryWord.word, dictionaryWord.cost),
                dictionaryWord = dictionaryWord
            )
        }
    }

    private suspend fun processUnlockWordResult(
        result: Resource<Boolean>, dictionaryWord: DictionaryWord
    ) {
        when (result) {
            is Resource.Error -> {
                _addEditDreamState.update {
                    it.copy(
                        bottomSheetState = false
                    )
                }
                _addEditDreamState.value.snackBarHostState.value.showSnackbar(
                    message = "${result.message}", actionLabel = "Dismiss"
                )
            }

            is Resource.Success -> {
                updateScreenStateForUnlockedWord(dictionaryWord)
                Log.d("DictionaryScreen", "Word unlocked successfully")
            }

            is Resource.Loading -> {
                // Handle loading state if needed
            }
        }
    }

    private fun updateScreenStateForUnlockedWord(dictionaryWord: DictionaryWord) {
        _addEditDreamState.update { state ->
            state.copy(isClickedWordUnlocked = true,
                clickedWord = dictionaryWord,
                unlockedWords = state.unlockedWords.apply {
                    add(dictionaryWord.word)
                })
        }
    }

    private fun getAIResponse(
        command: String,
        isAd: Boolean,
        cost: Int,
        activity: Activity,
        updateLoadingState: (Boolean) -> Unit,
        updateResponseState: (String) -> Unit
    ) {
        _addEditDreamState.value = addEditDreamState.value.copy(
            isDreamExitOff = true
        )
        viewModelScope.launch {
            if (contentTextFieldState.value.text.toString().length <= 20) {
                _addEditDreamState.value = addEditDreamState.value.copy(
                    isDreamExitOff = false
                )
                val message =
                    if (contentTextFieldState.value.text.toString().isEmpty()) {
                        "Dream content is empty"
                    } else {
                        "Dream content is too short"
                    }
                addEditDreamState.value.snackBarHostState.value.showSnackbar(
                    message, duration = SnackbarDuration.Short, actionLabel = "Dismiss"
                )
                return@launch
            }

            updateLoadingState(true)

            if (isAd) {
                runAd(activity, onRewardedAd = {
                    viewModelScope.launch {
                        makeAIRequest(command, 0, updateLoadingState, updateResponseState)
                        _addEditDreamState.value = addEditDreamState.value.copy(
                            isDreamExitOff = false
                        )
                    }
                }, onAdFailed = {
                    viewModelScope.launch {
                        _addEditDreamState.value = addEditDreamState.value.copy(
                            isDreamExitOff = false
                        )
                        addEditDreamState.value.snackBarHostState.value.showSnackbar(
                            "Ad failed to load",
                            duration = SnackbarDuration.Short,
                            actionLabel = "Dismiss"
                        )
                    }
                })
            } else {
                makeAIRequest(command, cost, updateLoadingState, updateResponseState)
                _addEditDreamState.value = addEditDreamState.value.copy(
                    isDreamExitOff = false
                )
            }
        }
    }

    private suspend fun makeAIRequest(
        command: String,
        cost: Int,
        updateLoadingState: (Boolean) -> Unit,
        updateResponseState: (String) -> Unit
    ) {
        try {
            val apiKey = getOpenAISecretKey()
            Log.d("AddEditDreamViewModel", "API Key: $apiKey")
            val openAI = OpenAI(apiKey)
            val currentLocale = Locale.getDefault().language

            val modelId = if (cost <= 0) "gpt-4o-mini" else "gpt-4o"
            val chatCompletionRequest = ChatCompletionRequest(
                model = ModelId(modelId), messages = listOf(
                    ChatMessage(
                        role = ChatRole.User,
                        content = "$command.\n Respond in this language: $currentLocale"
                    )
                ), maxTokens = 750
            )

            val completion = openAI.chatCompletion(chatCompletionRequest)
            updateResponseState(completion.choices.firstOrNull()?.message?.content ?: "")
            updateLoadingState(false)

            if (cost > 0) authRepository.consumeDreamTokens(cost)
        } catch (e: Exception) {
            Log.d("AddEditDreamViewModel", "Error: ${e.message}")
            updateLoadingState(false)
            _addEditDreamState.value.snackBarHostState.value.showSnackbar(
                "Error getting AI response", "Dismiss"
            )
        }
    }

    private fun getAIDetailsResponse(
        eventCost: Int
    ): Deferred<Unit> = viewModelScope.async {
        // Indicate loading state
        _addEditDreamState.value = addEditDreamState.value.copy(
            dreamAIImage = addEditDreamState.value.dreamAIImage.copy(
                isLoading = true
            )
        )

        val randomStyle =
            "A photograph of the scene, 4k, detailed, with vivid colors" + if (eventCost <= 1) {
                " and a very simple beautiful scene"
            } else {
                ""
            }

        val imagePrompt = if (eventCost <= 1) {
            "You are a dream environment builder: In the following dream, in third person and one short sentence 8 to 20 words build the visual elements, such as characters, scene, objects that stand out, or setting of the dream that follows. Make it short and straightforward: \n\n${
                contentTextFieldState.value.text
            } \n\nUse vivid imagery and a palette of rich, beautiful colors to highlight key objects or characters. Keep the description straightforward and focused on visuals only"
        } else {
            "You are a dream environment builder: In the following dream, in third person and one short sentence build the visual elements, such as characters, scene, objects that stand out, or setting of the dream that follows. Make it short and straightforward: \n\n${
                contentTextFieldState.value.text
            } \n\nUse vivid imagery and a palette of rich, beautiful colors to highlight key objects or characters."
        }


        val creativity = if (eventCost <= 1) {
            .4
        } else {
            1.1
        }


        Log.d("AddEditDreamViewModel", "Getting AI details response")
        try {
            val apiKey = getOpenAISecretKey()
            val openAI = OpenAI(apiKey)

            val chatCompletionRequest = ChatCompletionRequest(
                model = ModelId("gpt-4o"),
                messages = listOf(
                    ChatMessage(
                        role = ChatRole.User,
                        content = imagePrompt,
                    )
                ),
                maxTokens = 175,
                temperature = creativity,
            )

            val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
            Log.d(
                "AddEditDreamViewModel",
                "Response: ${completion.choices.firstOrNull()?.message?.content}"
            )
            // Update state with success
            _addEditDreamState.update { state ->
                state.copy(
                    dreamGeneratedDetails = state.dreamGeneratedDetails.copy(
                        response = (completion.choices.firstOrNull()?.message?.content + " $randomStyle"),
                        isLoading = false
                    )
                )
            }
        } catch (e: Exception) {
            // Handle error state
            Log.d("AddEditDreamViewModel", "Error: ${e.message}")
            _addEditDreamState.value = addEditDreamState.value.copy(
                dreamGeneratedDetails = addEditDreamState.value.dreamGeneratedDetails.copy(
                    isLoading = false
                )
            )
            // Optionally, show an error message to the user
        }
    }


    private fun getOpenAIImageResponse(
        cost: Int
    ): Deferred<Unit> = viewModelScope.async {
        // Indicate loading state
        _addEditDreamState.value = addEditDreamState.value.copy(
            dreamAIImage = addEditDreamState.value.dreamAIImage.copy(
                isLoading = true
            )
        )

        try {
            val apiKey = getOpenAISecretKey()
            val openAI = OpenAI(apiKey)

            val imageCreation = ImageCreation(
                prompt = addEditDreamState.value.dreamGeneratedDetails.response,
                model = ModelId(
                    if (cost <= 1) {
                        "dall-e-2"
                    } else "dall-e-3"
                ), // Adjust the model as per your requirement
                n = 1,
                size = if (cost <= 1) ImageSize.is512x512 else ImageSize.is1024x1024,
            )

            val images =
                openAI.imageURL(imageCreation) // Assuming imageURL returns a list of URLs

            // Assuming the first image's URL is what you need
            val imageUrl = images.firstOrNull()?.url ?: ""

            _addEditDreamState.value = addEditDreamState.value.copy(
                dreamAIImage = addEditDreamState.value.dreamAIImage.copy(
                    response = imageUrl, isLoading = false
                )
            )

            authRepository.consumeDreamTokens(cost)
            _addEditDreamState.value = addEditDreamState.value.copy(
                isDreamExitOff = false
            )
        } catch (e: Exception) {
            // Handle error state
            addEditDreamState.value.snackBarHostState.value.showSnackbar(
                "Error getting AI image response",
                duration = SnackbarDuration.Short,
                actionLabel = "Dismiss"
            )
            Log.d("AddEditDreamViewModel", "Error: ${e.message}")
            _addEditDreamState.value = addEditDreamState.value.copy(
                dreamAIImage = addEditDreamState.value.dreamAIImage.copy(
                    isLoading = false
                )
            )
            _addEditDreamState.value = addEditDreamState.value.copy(
                isDreamExitOff = false
            )
        }
    }


    private fun loadWords() {
        viewModelScope.launch(Dispatchers.IO) {
            val words = readDictionaryWordsFromCsv(application.applicationContext)

            Log.d("AddEditDreamViewModel", "Loaded words: ${words.size}")

            _addEditDreamState.update { state ->
                state.copy(
                    dictionaryWordMutableList = words.toMutableList()
                )
            }
        }
    }

    private fun dictionaryWordsInDreamFilterList(): List<DictionaryWord> {
        _addEditDreamState.value = addEditDreamState.value.copy(
            isDreamFilterLoading = true
        )
        val words = mutableListOf<DictionaryWord>()
        val dreamContent = contentTextFieldState.value.text.toString().lowercase(Locale.ROOT)

        // New logic for multi-word entries and single words with 5+ letters
        for (dictionary in addEditDreamState.value.dictionaryWordMutableList) {
            val dictionaryWordLower = dictionary.word.lowercase(Locale.getDefault())
            if (dictionaryWordLower.contains(" ") && dreamContent.contains(dictionaryWordLower)) {
                words.add(dictionary)
            } else if (!dictionaryWordLower.contains(" ") && dictionaryWordLower.length >= 5 && dreamContent.contains(
                    dictionaryWordLower
                )
            ) {
                words.add(dictionary)
            }
        }

        val dreamWords = dreamContent.split("\\s+".toRegex()).map { it.trim('.', '?', '\"', '\'') }

        val suffixes = listOf("ing", "ed", "er", "est", "s", "y")

        for (dreamWord in dreamWords) {
            if (dreamWord.isNotEmpty() && dreamWord.length > 2) {
                for (dictionary in addEditDreamState.value.dictionaryWordMutableList) {
                    val dictionaryWordLower = dictionary.word.lowercase(Locale.getDefault())
                    val possibleMatches = generatePossibleMatches(dreamWord, suffixes)

                    if (possibleMatches.contains(dictionaryWordLower) && !words.contains(dictionary)) {
                        words.add(dictionary)
                    } else {
                        val baseForm = removeSuffixes(dreamWord, suffixes)
                        if (baseForm == dictionaryWordLower && !words.contains(dictionary)) {
                            words.add(dictionary)
                        }
                    }
                }
            }
        }
        _addEditDreamState.value = addEditDreamState.value.copy(
            isDreamFilterLoading = false
        )
        return words.sortedBy { it.word }.distinct() // Added distinct to avoid duplicates
    }

    private fun generatePossibleMatches(baseWord: String, suffixes: List<String>): Set<String> {
        val matches = mutableSetOf<String>()
        if (baseWord.isNotEmpty()) {
            matches.add(baseWord) // Add the base word itself

            if (baseWord.length <= 3) {
                suffixes.forEach { suffix ->
                    matches.add(baseWord + baseWord.last() + suffix)
                }
            } else {
                suffixes.forEach { suffix ->
                    matches.add(baseWord + suffix)
                    matches.add(baseWord + baseWord.last() + suffix)
                    if (baseWord.last() != suffix.first()) {
                        matches.add(baseWord.dropLast(1) + suffix)
                    }
                }
            }
        }
        return matches
    }

    private fun removeSuffixes(word: String, suffixes: List<String>): String {
        var baseForm = word
        suffixes.forEach { suffix ->
            if (word.endsWith(suffix)) {
                baseForm = word.removeSuffix(suffix)
                return@forEach
            }
        }
        return baseForm
    }


    private fun readDictionaryWordsFromCsv(context: Context): List<DictionaryWord> {
        val words = mutableListOf<DictionaryWord>()
        val csvRegex = """"(.*?)"|([^,]+)""".toRegex() // Matches quoted strings or unquoted tokens
        try {
            context.assets.open("dream_dictionary.csv").bufferedReader().useLines { lines ->
                lines.drop(1).forEach { line ->
                    val tokens = csvRegex.findAll(line).map { it.value.trim('"') }.toList()
                    if (tokens.size >= 3) {
                        val cost =
                            tokens.last().toIntOrNull() ?: 0 // Assuming cost is the last token
                        words.add(
                            DictionaryWord(
                                word = tokens.first(), // Assuming word is the first token
                                definition = tokens.drop(1).dropLast(1)
                                    .joinToString(","), // Joining all tokens that are part of the definition
                                isUnlocked = cost == 0, // If cost is 0, then the word is unlocked
                                cost = cost
                            )
                        )
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return words
    }


    private fun runAd(
        activity: Activity, onRewardedAd: () -> Unit, onAdFailed: () -> Unit
    ) {
        activity.runOnUiThread {
            adManagerRepository.loadRewardedAd(activity) {
                //show ad
                adManagerRepository.showRewardedAd(activity, object : AdCallback {
                    override fun onAdClosed() {
                        viewModelScope.launch {
                            addEditDreamState.value.snackBarHostState.value.showSnackbar(
                                "Ad closed",
                                duration = SnackbarDuration.Short,
                                actionLabel = "Dismiss"
                            )
                        }
                    }

                    override fun onAdRewarded(reward: RewardItem) {
                        onRewardedAd()
                    }

                    override fun onAdLeftApplication() {
                        viewModelScope.launch {
                            addEditDreamState.value.snackBarHostState.value.showSnackbar(
                                "Ad left application",
                                duration = SnackbarDuration.Short,
                                actionLabel = "Dismiss"
                            )
                        }
                    }

                    override fun onAdLoaded() {}

                    override fun onAdFailedToLoad(errorCode: Int) {
                        onAdFailed()
                    }

                    override fun onAdOpened() {
                        TODO("Not yet implemented")
                    }
                })
            }
        }
    }
}

data class AddEditDreamState(
    val dreamInfo: DreamInfo = DreamInfo(
        dreamId = "",
        dreamUID = "",
        dreamBackgroundImage = Dream.dreamBackgroundImages.indices.random(),
        dreamIsLucid = false,
        dreamIsFavorite = false,
        dreamIsNightmare = false,
        dreamIsRecurring = false,
        dreamIsFalseAwakening = false,
        dreamSleepTime = formatLocalTime(sleepTime),    // "11:00 PM"
        dreamWakeTime = formatLocalTime(wakeTime),      // "7:00 AM"
        dreamDate = formatLocalDate(currentDate),        // "Jun 3, 2023" for example
        dreamTimeOfDay = "",
        dreamLucidity = 0,
        dreamVividness = 0,
        dreamEmotion = 0
    ),
    val dreamContentChanged: Boolean = true,
    val dreamAIExplanation: DreamAIExplanation = DreamAIExplanation(
        response = "",
        isLoading = false,
        error = "",
    ),
    val dreamAIImage: DreamAIImage = DreamAIImage(
        response = "", isLoading = false, error = ""
    ),
    val dreamAIAdvice: DreamAIAdvice = DreamAIAdvice(
        response = "",
        isLoading = false,
        error = "",
    ),
    val dreamGeneratedDetails: DreamAIGeneratedDetails = DreamAIGeneratedDetails(
        response = "", isLoading = false, isSuccessful = false, error = ""
    ),
    val dreamAIQuestionAnswer: DreamQuestionAIAnswer = DreamQuestionAIAnswer(
        response = "", isLoading = false, error = ""
    ),
    val dreamAIStory: DreamStoryGeneration = DreamStoryGeneration(
        response = "", isLoading = false, error = ""
    ),
    val dreamAIMoodAnalyser: DreamMoodAIAnalyser = DreamMoodAIAnalyser(
        response = "", isLoading = false, error = ""
    ),
    val dreamIsSavingLoading: Boolean = false,
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false,
    val dialogState: Boolean = false,
    val calendarDialogState: Boolean = false,
    val sleepTimePickerDialogState: Boolean = false,
    val wakeTimePickerDialogState: Boolean = false,
    val dreamImageGenerationPopUpState: Boolean = false,
    val dreamInterpretationPopUpState: Boolean = false,
    val dreamAdvicePopUpState: Boolean = false,
    val dreamQuestionPopUpState: Boolean = false,
    val dreamStoryPopupState: Boolean = false,
    val dreamMoodPopupState: Boolean = false,
    val isDreamExitOff: Boolean = false,
    val snackBarHostState: MutableState<SnackbarHostState> = mutableStateOf(SnackbarHostState()),
    val dictionaryWordMutableList: MutableList<DictionaryWord> = mutableListOf(),
    val dreamFilteredDictionaryWords: List<DictionaryWord> = mutableListOf(),
    val unlockedWords: MutableList<String> = mutableListOf(),
    val bottomSheetState: Boolean = false,
    val clickedWord: DictionaryWord = DictionaryWord("", "", false, 0),
    val isClickedWordUnlocked: Boolean = false,
    val isDreamFilterLoading: Boolean = false,
    val authRepository: AuthRepository,
    val dreamTokens: Int = 0,
    val dreamHasChanged: Boolean = false,
)

data class DreamAIExplanation(
    override val response: String = "",
    override val isLoading: Boolean = false,
    override val error: String? = null,
) : AIData

data class DreamAIGeneratedDetails(
    override val response: String = "",
    override val isLoading: Boolean = false,
    val isSuccessful: Boolean = false, // This remains specific to this class
    override val error: String? = null,
) : AIData

data class DreamAIImage(
    override val response: String = "", // Specific to this class
    override val isLoading: Boolean = false,
    override val error: String? = null
) : AIData

data class DreamAIAdvice(
    override val response: String = "",
    override val isLoading: Boolean = false,
    override val error: String? = null,
) : AIData

data class DreamQuestionAIAnswer(
    override val response: String = "",
    val question: String = "", // Specific to this class
    override val isLoading: Boolean = false,
    override val error: String? = null,
) : AIData

data class DreamStoryGeneration(
    override val response: String = "",
    override val isLoading: Boolean = false,
    override val error: String? = null,
) : AIData

data class DreamMoodAIAnalyser(
    override val response: String = "",
    override val isLoading: Boolean = false,
    override val error: String? = null,
) : AIData

data class DreamInfo(
    val dreamId: String?,
    val dreamUID: String?,
    var dreamBackgroundImage: Int,
    val dreamIsLucid: Boolean,
    val dreamIsFavorite: Boolean,
    val dreamIsNightmare: Boolean,
    val dreamIsRecurring: Boolean,
    val dreamIsFalseAwakening: Boolean,
    val dreamSleepTime: String,
    val dreamWakeTime: String,
    val dreamDate: String,
    val dreamTimeOfDay: String,
    val dreamLucidity: Int,
    val dreamVividness: Int,
    val dreamEmotion: Int,
)

interface AIData {
    val response: String
    val isLoading: Boolean
    val error: String?
}