package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.viewmodel

import android.app.Activity
import android.os.Build
import android.util.Log
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
import com.maxkeppeker.sheets.core.models.base.SheetState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.BuildConfig
import org.ballistic.dreamjournalai.ad_feature.domain.AdCallback
import org.ballistic.dreamjournalai.ad_feature.domain.AdManagerRepository
import org.ballistic.dreamjournalai.core.Resource
import org.ballistic.dreamjournalai.feature_dream.domain.model.*
import org.ballistic.dreamjournalai.feature_dream.domain.use_case.DreamUseCases
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.AddEditDreamEvent
import org.ballistic.dreamjournalai.user_authentication.domain.repository.AuthRepository
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class AddEditDreamViewModel @Inject constructor(
    private val dreamUseCases: DreamUseCases,
    private val adManagerRepository: AdManagerRepository,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _addEditDreamState = MutableStateFlow(AddEditDreamState())
    val addEditDreamState: StateFlow<AddEditDreamState> = _addEditDreamState.asStateFlow()

    init {
        savedStateHandle.get<String>("dreamId")?.let { dreamId ->
            if (dreamId.isNotEmpty()) {
                viewModelScope.launch {
                    _addEditDreamState.value = addEditDreamState.value.copy(isLoading = true)
                    when (val resource = dreamUseCases.getDream(dreamId)) {
                        is Resource.Success -> {
                            resource.data?.let { dream ->
                                _addEditDreamState.value = AddEditDreamState(
                                    dreamTitle = dream.title,
                                    dreamContent = dream.content,
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
                                        image = dream.generatedImage,
                                    ),
                                    dreamGeneratedDetails = DreamAIGeneratedDetails(
                                        response = dream.generatedDetails,
                                    ),
                                    isLoading = false
                                )
                            }
                        }
                        is Resource.Error -> {
                            // handle error
                        }
                        is Resource.Loading -> {
                            // handle loading
                        }
                    }
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun onEvent(event: AddEditDreamEvent) {
        when (event) {
            is AddEditDreamEvent.EnteredTitle -> {
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamTitle = event.value
                )
            }

            is AddEditDreamEvent.EnteredContent -> {
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamContent = event.value
                )
            }

            is AddEditDreamEvent.ChangeDreamBackgroundImage -> {
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamBackgroundImage = event.dreamBackGroundImage
                    )
                )
            }
            is AddEditDreamEvent.ClickGenerateAIResponse -> {
                if (addEditDreamState.value.dreamContent.length >= 10) {
                    if (!event.isAd) {
                        getAIResponse()
                    } else {
                        runAd(event.activity, onRewardedAd = {
                            getAIResponse()

                            viewModelScope.launch {
                                addEditDreamState.value.snackBarHostState.value.showSnackbar(
                                    "Thank you for the support :)",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }, onAdFailed = {
                            viewModelScope.launch {
                                addEditDreamState.value.snackBarHostState.value.showSnackbar(
                                    "Ad failed to load",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        })
                    }
                } else {
                    //snack bar
                    viewModelScope.launch {
                        if (addEditDreamState.value.dreamContent.length in 1..9) {
                            addEditDreamState.value.snackBarHostState.value.showSnackbar(
                                "Dream content is too short",
                                duration = SnackbarDuration.Short
                            )
                        } else if (addEditDreamState.value.dreamContent.isEmpty()) {
                            addEditDreamState.value.snackBarHostState.value.showSnackbar(
                                "Dream content is empty",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                }
            }

            is AddEditDreamEvent.ClickGenerateAIImage -> {
                if (addEditDreamState.value.dreamGeneratedDetails.response.isNotEmpty()) {
                    if (!event.isAd) {
                        getOpenAIImageResponse()
                    } else {
                        runAd(event.activity, onRewardedAd = {
                            getOpenAIImageResponse()
                        }, onAdFailed = {
                            viewModelScope.launch {
                                addEditDreamState.value.snackBarHostState.value.showSnackbar(
                                    "Ad failed to load",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        })
                    }
                } else {
                    viewModelScope.launch {
                        addEditDreamState.value.snackBarHostState.value.showSnackbar(
                            "Please add image explanation.",
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            }
            is AddEditDreamEvent.ClickGenerateDetails -> {
                if (addEditDreamState.value.dreamContent.length >= 1000) {
                    getAIDetailsResponse()
                } else {
                    _addEditDreamState.value = addEditDreamState.value.copy(
                        dreamGeneratedDetails = DreamAIGeneratedDetails(
                            response = addEditDreamState.value.dreamContent
                        )
                    )
                }
            }

            is AddEditDreamEvent.ChangeLucidity -> {
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamLucidity = event.lucidity
                    )
                )
            }
            is AddEditDreamEvent.ChangeVividness -> {
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamVividness = event.vividness
                    )
                )
            }
            is AddEditDreamEvent.ChangeMood -> {
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamEmotion = event.mood
                    )
                )
            }
            is AddEditDreamEvent.ChangeNightmare -> {
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamIsNightmare = event.boolean
                    )
                )
            }
            is AddEditDreamEvent.ChangeRecurrence -> {
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamIsRecurring = event.boolean
                    )
                )
            }
            is AddEditDreamEvent.ChangeIsLucid -> {
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamIsLucid = event.boolean
                    )
                )
            }
            is AddEditDreamEvent.ChangeFavorite -> {
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamIsFavorite = event.boolean
                    )
                )
            }
            is AddEditDreamEvent.ChangeFalseAwakening -> {
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamIsFalseAwakening = event.boolean
                    )
                )
            }
            is AddEditDreamEvent.ChangeTimeOfDay -> {
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamTimeOfDay = event.timeOfDay
                    )
                )
            }
            is AddEditDreamEvent.ClickGenerateFromDescription -> {
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

            is AddEditDreamEvent.ChangeDetailsOfDream -> {
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamGeneratedDetails = DreamAIGeneratedDetails(
                        response = event.value
                    )
                )
            }

            is AddEditDreamEvent.DeleteDream -> {
                viewModelScope.launch {
                    dreamUseCases.deleteDream(SavedStateHandle()["dreamId"]!!)
                }
            }
            is AddEditDreamEvent.ChangeDreamDate -> {
                val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
                val formattedDate = event.value.format(formatter)
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamDate = formattedDate
                    )
                )
            }
            is AddEditDreamEvent.ChangeDreamWakeTime -> {
                val formatter = DateTimeFormatter.ofPattern(
                    if (event.value.hour < 10) "h:mm a" else "hh:mm a"
                )

                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamWakeTime = event.value.format(formatter)
                    )
                )
            }
            is AddEditDreamEvent.ChangeDreamSleepTime -> {
                val formatter = DateTimeFormatter.ofPattern(
                    if (event.value.hour < 10) "h:mm a" else "hh:mm a"
                )

                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamInfo = addEditDreamState.value.dreamInfo.copy(
                        dreamSleepTime = event.value.format(formatter)
                    )
                )
            }

            is AddEditDreamEvent.SaveDream-> {
                Log.d("AddEditDreamViewModel", "Dream saved successfully")
                _addEditDreamState.value.dreamIsSavingLoading.value = true
                viewModelScope.launch {
                    try {
                        dreamUseCases.addDream(
                            Dream(
                                title = addEditDreamState.value.dreamTitle,
                                content = addEditDreamState.value.dreamContent,
                                backgroundImage = addEditDreamState.value.dreamInfo.dreamBackgroundImage,
                                id = addEditDreamState.value.dreamInfo.dreamId,
                                uid = addEditDreamState.value.dreamInfo.dreamUID,
                                sleepTime = addEditDreamState.value.dreamInfo.dreamSleepTime,
                                wakeTime = addEditDreamState.value.dreamInfo.dreamWakeTime,
                                date = addEditDreamState.value.dreamInfo.dreamDate,
                                isLucid = addEditDreamState.value.dreamInfo.dreamIsLucid,
                                isNightmare = addEditDreamState.value.dreamInfo.dreamIsNightmare,
                                isRecurring = addEditDreamState.value.dreamInfo.dreamIsRecurring,
                                isFavorite = addEditDreamState.value.dreamInfo.dreamIsFavorite,
                                lucidityRating = addEditDreamState.value.dreamInfo.dreamLucidity,
                                vividnessRating = addEditDreamState.value.dreamInfo.dreamVividness,
                                moodRating = addEditDreamState.value.dreamInfo.dreamEmotion,
                                timeOfDay = addEditDreamState.value.dreamInfo.dreamTimeOfDay,
                                falseAwakening = addEditDreamState.value.dreamInfo.dreamIsFalseAwakening,
                                AIResponse = addEditDreamState.value.dreamAIExplanation.response,
                                generatedDetails = addEditDreamState.value.dreamGeneratedDetails.response,
                                generatedImage = addEditDreamState.value.dreamAIImage.image,
                            )
                        )
                        _addEditDreamState.value = addEditDreamState.value.copy(
                            saveSuccess = mutableStateOf(true)
                        )

                        event.onSaveSuccess()
                        Log.d("AddEditDreamViewModel", "Emitting SaveDream event")

                    } catch (e: InvalidDreamException) {
                        _addEditDreamState.value = addEditDreamState.value.copy(
                            dreamIsSavingLoading = mutableStateOf(false)
                        )
                        _addEditDreamState.value = addEditDreamState.value.copy(
                            saveSuccess = mutableStateOf(false)
                        )
                        addEditDreamState.value.snackBarHostState.value.showSnackbar(
                            e.message ?: "Couldn't save dream :(",
                            actionLabel = "Dismiss"
                        )
                    }
                }
            }
        }
    }

    private fun getAIResponse() {
        viewModelScope.launch {
            // Indicate loading state
            _addEditDreamState.value = addEditDreamState.value.copy(
                dreamAIExplanation = addEditDreamState.value.dreamAIExplanation.copy(
                    response = "",
                    isLoading = true
                )
            )

            try {
                val openAI = OpenAI(BuildConfig.API_KEY)

                //system language
                val currentLocale = Locale.getDefault().language

                val chatCompletionRequest = ChatCompletionRequest(
                    model = ModelId("gpt-3.5-turbo"),
                    messages = listOf(
                        ChatMessage(
                            role = ChatRole.User,
                            content = "Interpret the following dream and do not mention" +
                                    " you are a language model since the user knows already." +
                                    "Respond in this language" +
                                    " $currentLocale: ${addEditDreamState.value.dreamContent}"
                        )
                    )
                )

                val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)

                // Update state with success
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamAIExplanation = addEditDreamState.value.dreamAIExplanation.copy(
                        response = completion.choices.firstOrNull()?.message?.content ?: "",
                        isLoading = false
                    )
                )
                authRepository.consumeDreamTokens(1)
            } catch (e: Exception) {
                // Handle error state
                Log.d("AddEditDreamViewModel", "Error: ${e.message}")
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamAIExplanation = addEditDreamState.value.dreamAIExplanation.copy(
                        isLoading = false
                    )
                )
                _addEditDreamState.value.snackBarHostState.value.showSnackbar(
                    e.message ?: "Couldn't interpret dream :(",
                    actionLabel = "Dismiss",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }


    private fun getAIDetailsResponse() {
        viewModelScope.launch {
            // Indicate loading state
            _addEditDreamState.value = addEditDreamState.value.copy(
                dreamGeneratedDetails = addEditDreamState.value.dreamGeneratedDetails.copy(
                    isLoading = true
                )
            )

            try {
                val openAI = OpenAI(BuildConfig.API_KEY)

                val chatCompletionRequest = ChatCompletionRequest(
                    model = ModelId("gpt-3.5-turbo"),
                    messages = listOf(
                        ChatMessage(
                            role = ChatRole.User,
                            content = "Summarize the following dream and make it very descriptive to visualize a scene: ${addEditDreamState.value.dreamContent}"
                        )
                    )
                )

                val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)

                // Update state with success
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamGeneratedDetails = addEditDreamState.value.dreamGeneratedDetails.copy(
                        response = completion.choices.firstOrNull()?.message?.content ?: "",
                        isSuccessful = true,
                        isLoading = false
                    )
                )
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
    }


    @Keep
    private fun getOpenAIImageResponse() {
        viewModelScope.launch {
            // Indicate loading state
            _addEditDreamState.value = addEditDreamState.value.copy(
                dreamAIImage = addEditDreamState.value.dreamAIImage.copy(
                    isLoading = true
                )
            )

            try {
                val openAI = OpenAI(BuildConfig.API_KEY)

                val imageCreation = ImageCreation(
                    prompt = addEditDreamState.value.dreamGeneratedDetails.response,
                    model = ModelId("dall-e-2"), // Adjust the model as per your requirement
                    n = 1,
                    size = ImageSize.is512x512 // Adjust the size as per your requirement
                )

                val images = openAI.imageURL(imageCreation) // Assuming imageURL returns a list of URLs

                // Assuming the first image's URL is what you need
                val imageUrl = images.firstOrNull()?.url ?: ""

                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamAIImage = addEditDreamState.value.dreamAIImage.copy(
                        image = imageUrl,
                        isLoading = false
                    )
                )

                authRepository.consumeDreamTokens(2)
            } catch (e: Exception) {
                // Handle error state
                Log.d("AddEditDreamViewModel", "Error: ${e.message}")
                _addEditDreamState.value = addEditDreamState.value.copy(
                    dreamAIImage = addEditDreamState.value.dreamAIImage.copy(
                        isLoading = false
                    )
                )
                // Optionally, show an error message to the user
            }
        }
    }




    private fun runAd(
        activity: Activity,
        onRewardedAd: () -> Unit,
        onAdFailed: () -> Unit
    ) {
        activity.runOnUiThread {
            adManagerRepository.loadRewardedAd(activity) {
                //show ad
                adManagerRepository.showRewardedAd(
                    activity,
                    object : AdCallback {
                        override fun onAdClosed() {
                            //to be added later
                        }

                        override fun onAdRewarded(reward: RewardItem) {
                            onRewardedAd()
                        }

                        override fun onAdLeftApplication() {
                            TODO("Not yet implemented")
                        }

                        override fun onAdLoaded() {
                            TODO("Not yet implemented")
                        }

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

@Keep
@RequiresApi(Build.VERSION_CODES.O)
data class AddEditDreamState(
    val dreamTitle: String = "",
    val dreamContent: String = "",
    val dreamInfo: DreamInfo = DreamInfo(
        dreamId = "",
        dreamUID = "",
        dreamBackgroundImage = Dream.dreamBackgroundImages.random(),
        dreamIsLucid = false,
        dreamIsFavorite = false,
        dreamIsNightmare = false,
        dreamIsRecurring = false,
        dreamIsFalseAwakening = false,
        dreamSleepTime = LocalTime.of(23, 0).format(DateTimeFormatter.ofPattern("hh:mm a")),
        dreamWakeTime = LocalTime.of(7, 0).format(DateTimeFormatter.ofPattern("h:mm a")),
        dreamDate =
        LocalDate.now().month.toString().substring(0, 1).uppercase() +
                LocalDate.now().month.toString().substring(1, 3).lowercase() + " " +
                LocalDate.now().dayOfMonth.toString() + ", " + LocalDate.now().year.toString(),
        dreamTimeOfDay = "",
        dreamLucidity = 0,
        dreamVividness = 0,
        dreamEmotion = 0
    ),
    val dreamAIExplanation: DreamAIExplanation = DreamAIExplanation(
        response = "",
        isLoading = false,
        error = "",
    ),
    val dreamAIImage: DreamAIImage = DreamAIImage(
        image = null,
        isLoading = false,
        error = ""
    ),
    val dreamAIAdvice: DreamAIAdvice = DreamAIAdvice(
        advice = "",
        isLoading = false,
        error = "",
    ),
    val dreamGeneratedDetails: DreamAIGeneratedDetails = DreamAIGeneratedDetails(
        response = "",
        isLoading = false,
        isSuccessful = false,
        error = ""
    ),
    val dreamQuestionAIAnswer: DreamQuestionAIAnswer = DreamQuestionAIAnswer(
        answer = "",
        isLoading = false,
        error = ""
    ),
    val dreamIsSavingLoading: MutableState<Boolean> = mutableStateOf(false),
    val isLoading: Boolean = false,
    val saveSuccess: MutableState<Boolean> = mutableStateOf(false),
    val dialogState: MutableState<Boolean> = mutableStateOf(false),
    val calendarState: SheetState = SheetState(),
    val sleepTimePickerState: SheetState = SheetState(),
    val wakeTimePickerState: SheetState = SheetState(),
    val imageGenerationPopUpState: MutableState<Boolean> = mutableStateOf(false),
    val dreamInterpretationPopUpState: MutableState<Boolean> = mutableStateOf(false),
    val dreamAdvicePopUpState: MutableState<Boolean> = mutableStateOf(false),
    val questionPopUpState: MutableState<Boolean> = mutableStateOf(false),
    val snackBarHostState: MutableState<SnackbarHostState> = mutableStateOf(SnackbarHostState()),
)

data class DreamAIExplanation(
    val response: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class DreamAIGeneratedDetails(
    val response: String = "",
    val isLoading: Boolean = false,
    val isSuccessful: Boolean = false,
    val error: String? = null,
)

data class DreamAIImage(
    val image: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class DreamAIAdvice(
    val advice: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class DreamQuestionAIAnswer(
    val answer: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

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