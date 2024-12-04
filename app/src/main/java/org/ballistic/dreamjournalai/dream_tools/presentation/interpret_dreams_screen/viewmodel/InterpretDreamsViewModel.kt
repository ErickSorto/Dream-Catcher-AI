package org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen.viewmodel

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.google.android.gms.ads.rewarded.RewardItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.core.Resource
import org.ballistic.dreamjournalai.dream_ad.domain.AdCallback
import org.ballistic.dreamjournalai.dream_ad.domain.AdManagerRepository
import org.ballistic.dreamjournalai.core.util.OpenAIApiKeyUtil.getOpenAISecretKey
import org.ballistic.dreamjournalai.dream_tools.domain.MassInterpretationRepository
import org.ballistic.dreamjournalai.dream_tools.domain.event.InterpretDreamsToolEvent
import org.ballistic.dreamjournalai.dream_tools.domain.model.MassInterpretation
import org.ballistic.dreamjournalai.dream_journal_list.domain.model.Dream
import org.ballistic.dreamjournalai.dream_journal_list.domain.use_case.DreamUseCases
import org.ballistic.dreamjournalai.dream_journal_list.domain.util.OrderType
import org.ballistic.dreamjournalai.dream_authentication.domain.repository.AuthRepository
import java.util.Locale

class InterpretDreamsViewModel(
    private val dreamUseCases: DreamUseCases,
    private val authRepository: AuthRepository,
    private val massInterpretationRepository: MassInterpretationRepository,
    private val adManagerRepository: AdManagerRepository
) : ViewModel() {
    private val _interpretDreamsScreenState = MutableStateFlow(InterpretDreamsScreenState(
        authRepository = authRepository
    ))
    val interpretDreamsScreenState: StateFlow<InterpretDreamsScreenState> =
        _interpretDreamsScreenState.asStateFlow()

    private var getDreamJob: Job? = null
    fun onEvent(event: InterpretDreamsToolEvent) {
        when (event) {

            is InterpretDreamsToolEvent.GetDreams -> {
                viewModelScope.launch {
                    getDreams()
                }
            }

            is InterpretDreamsToolEvent.ToggleDreamToInterpretationList -> {
                viewModelScope.launch {
                    toggleDreamToInterpretationList(event.dream)
                }
            }

            is InterpretDreamsToolEvent.DeleteMassInterpretation -> {
                viewModelScope.launch {
                    massInterpretationRepository.removeInterpretation(event.massInterpretation)
                }
            }

            is InterpretDreamsToolEvent.InterpretDreams -> {

                // Logging the start of the function
                Log.d("InterpretDreams", "Starting to interpret dreams")

                // Getting the current list of dreams
                val currentDreams = interpretDreamsScreenState.value.chosenDreams

                viewModelScope.launch {
                    getAIResponse(
                        command = "Find common themes, symbols, and meanings in the following dreams:\n" +
                                currentDreams.joinToString("\n") { it.content },
                        cost = event.cost,
                        isAd = event.isAd,
                        updateLoadingState = { isLoading ->
                            _interpretDreamsScreenState.update {
                                it.copy(isLoading = isLoading)
                            }
                        },
                        activity = event.activity,
                        updateResponseState = { response ->
                            _interpretDreamsScreenState.update {
                                it.copy(response = response)
                            }
                            event.isFinishedEvent(true)
                        },
                        handleError = {
                            _interpretDreamsScreenState.update {
                                it.copy(response = it.response)
                            }
                            event.isFinishedEvent(true)
                        }
                    )
                }
            }

            InterpretDreamsToolEvent.GetMassInterpretations -> {
                viewModelScope.launch {
                    massInterpretationRepository.getInterpretations()
                        .onEach { interpretations ->
                            _interpretDreamsScreenState.value =
                                interpretDreamsScreenState.value.copy(
                                    massMassInterpretations = interpretations
                                )
                        }
                        .catch { exception ->
                            Log.e(
                                "GetMassInterpretations",
                                "Error fetching mass interpretations",
                                exception
                            )
                        }
                        .launchIn(viewModelScope)
                }
            }

            is InterpretDreamsToolEvent.ToggleBottomMassInterpretationSheetState -> {
                _interpretDreamsScreenState.update {
                    it.copy(bottomMassInterpretationSheetState = event.state)
                }
            }
            is InterpretDreamsToolEvent.UpdateModel -> {
                _interpretDreamsScreenState.update {
                    it.copy(modelChosen = event.model)
                }
            }

            is InterpretDreamsToolEvent.ChooseMassInterpretation -> {
                _interpretDreamsScreenState.update {
                    it.copy(chosenMassInterpretation = event.massInterpretation)
                }
            }

            is InterpretDreamsToolEvent.ToggleBottomDeleteCancelSheetState -> {
                _interpretDreamsScreenState.update {
                    it.copy(bottomDeleteCancelSheetState = event.state)
                }
            }
            is InterpretDreamsToolEvent.GetDreamTokens -> {
                viewModelScope.launch {
                    authRepository.addDreamTokensFlowListener().collect { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                _interpretDreamsScreenState.update {
                                    it.copy(dreamTokens = resource.data?.toInt() ?: 0)
                                }
                            }
                            is Resource.Error -> {
                                Log.e("GetDreamTokens", "Error fetching dream tokens")
                            }
                            is Resource.Loading -> {
                                Log.d("GetDreamTokens", "Fetching dream tokens")
                            }
                        }
                    }
                }
            }
        }
    }

    //add or remove dream from interpretation list
    private fun toggleDreamToInterpretationList(dream: Dream) {
        // Logging the start of the function
        Log.d("toggleDreamToInterpretationList", "Attempting to modify dream list")

        // Getting the current list of dreams
        val currentDreams = interpretDreamsScreenState.value.chosenDreams.toMutableList()

        if (currentDreams.contains(dream)) {
            // Removing the dream from the list
            currentDreams.remove(dream)
            Log.d("toggleDreamToInterpretationList", "Removed dream from list: ${dream.id}")
        } else {
            // Ensure we can add a new dream only if we haven't reached the limit
            if (currentDreams.size < 15) {
                currentDreams.add(dream)
                Log.d("toggleDreamToInterpretationList", "Added dream to list: ${dream.id}")
            } else {
                Log.d("toggleDreamToInterpretationList", "Dream list is full. Cannot add more dreams.")
                return // Prevent adding more dreams if the list is full
            }
        }

        // Update the state with the modified list of dreams
        _interpretDreamsScreenState.value = interpretDreamsScreenState.value.copy(
            chosenDreams = currentDreams
        )
    }


    private fun getDreams() {
        // Logging the start of the function
        Log.d("getDreams", "Starting to fetch dreams")

        getDreamJob?.cancel()
        getDreamJob = dreamUseCases.getDreams(OrderType.Date)
            .onEach { dreams ->
                // Logging when dreams are received
                Log.d("getDreams", "Received dreams: ${dreams.size} items")

                _interpretDreamsScreenState.value = interpretDreamsScreenState.value.copy(
                    dreams = dreams
                )
                // Logging after updating the screen state
                Log.d("getDreams", "Updated randomDreamScreen with new dreams")
            }
            .catch { exception ->
                // Logging in case of an error
                Log.e("getDreams", "Error fetching dreams", exception)
            }
            .launchIn(viewModelScope)


        // Logging the event trigger
        Log.d("getDreams", "LoadStatistics event triggered")
    }

    private fun getAIResponse(
        command: String,
        isAd: Boolean,
        cost: Int,
        activity: Activity,
        updateLoadingState: (Boolean) -> Unit,
        updateResponseState: (String) -> Unit,
        handleError: (String) -> Unit
    ) {

        viewModelScope.launch {
            updateLoadingState(true)

            if (isAd) {
                runAd(activity, onRewardedAd = {
                    viewModelScope.launch {
                        makeAIRequest(command, 0, updateLoadingState, updateResponseState,
                            handleError = {
                                handleError(it)
                            }
                            )
                    }
                }, onAdFailed = {
                    viewModelScope.launch {

                    }
                })
            } else {
                makeAIRequest(command, cost, updateLoadingState, updateResponseState,
                    handleError = {
                        handleError(it)
                    }
                )
            }
        }
    }

    private suspend fun makeAIRequest(
        command: String,
        cost: Int,
        updateLoadingState: (Boolean) -> Unit,
        updateResponseState: (String) -> Unit,
        handleError: (String) -> Unit
    ) {
        try {
            updateLoadingState(true)
            val apiKey = getOpenAISecretKey()
            val openAI = OpenAI(apiKey)
            val currentLocale = Locale.getDefault().language

            val modelId = interpretDreamsScreenState.value.modelChosen
            val chatCompletionRequest = ChatCompletionRequest(
                model = ModelId(modelId), messages = listOf(
                    ChatMessage(
                        role = ChatRole.User,
                        content = "$command.\n Respond in this language: $currentLocale"
                    )
                ), maxTokens = 500
            )

            val completion = openAI.chatCompletion(chatCompletionRequest)
            updateResponseState(completion.choices.firstOrNull()?.message?.content ?: "")

            if (cost > 0) authRepository.consumeDreamTokens(cost)
            val massInterpretation = MassInterpretation(
                interpretation = completion.choices.firstOrNull()?.message?.content ?: "",
                listOfDreamIDs = interpretDreamsScreenState.value.chosenDreams.map { it.id },
                date = System.currentTimeMillis(),
                model = if (modelId == "gpt-4o") "Advanced" else "Standard",
                id = null
            )
            massInterpretationRepository.addInterpretation(
                massInterpretation
            )
            _interpretDreamsScreenState.value =
                interpretDreamsScreenState.value.copy(
                    chosenMassInterpretation = massInterpretation
                )
            updateLoadingState(false)
        } catch (e: Exception) {
            Log.d("AddEditDreamViewModel", "Error: ${e.message}")
            updateLoadingState(false)
            handleError(e.message ?: "An error occurred")
        }
    }
    private fun runAd(
        activity: Activity, onRewardedAd: () -> Unit, onAdFailed: () -> Unit
    ) {
        activity.runOnUiThread {
            adManagerRepository.loadRewardedAd(activity) {
                //show ad
                adManagerRepository.showRewardedAd(activity, object : AdCallback {
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

data class InterpretDreamsScreenState(
    val dreams: List<Dream> = emptyList(),
    val modelChosen: String = "gpt-4o-mini",
    val authRepository: AuthRepository,
    val massMassInterpretations: List<MassInterpretation> = emptyList(),
    val chosenMassInterpretation: MassInterpretation = MassInterpretation(),
    val chosenDreams: List<Dream> = emptyList(),
    val isLoading: Boolean = false,
    val response: String = "",
    val bottomMassInterpretationSheetState: Boolean = false,
    val bottomDeleteCancelSheetState: Boolean = false,
    val dreamTokens: Int = 0
)

