package org.ballistic.dreamjournalai.shared.dream_add_edit.presentation


import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dreamjournalai.composeapp.shared.generated.resources.Res
import dreamjournalai.composeapp.shared.generated.resources.save_dream
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.model.Dream
import org.ballistic.dreamjournalai.shared.dream_main.domain.MainScreenEvent
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.LightBlack
import org.ballistic.dreamjournalai.shared.theme.OriginalXmlColors.White
import org.ballistic.dreamjournalai.shared.core.components.dynamicBottomNavigationPadding
import org.ballistic.dreamjournalai.shared.core.util.BackHandler
import org.ballistic.dreamjournalai.shared.dream_add_edit.domain.AddEditDreamEvent
import org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.components.AlertSave
import org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.components.TabLayout
import org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.viewmodel.AddEditDreamState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.AddEditDreamScreen(
    dreamImage: Int,
    dreamTitleState: TextFieldState,
    dreamContentState: TextFieldState,
    addEditDreamState: AddEditDreamState,
    animateVisibilityScope: AnimatedVisibilityScope,
    onMainEvent: (MainScreenEvent) -> Unit = {},
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit = {},
    onNavigateToDreamJournalScreen: () -> Unit = {},
    onImageClick: (String) -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    listOf(
        MainScreenEvent.SetBottomBarVisibilityState(false),
        MainScreenEvent.SetFloatingActionButtonState(false),
        MainScreenEvent.SetSearchingState(false),
        MainScreenEvent.SetDrawerState(false)
    ).forEach(onMainEvent)

    val scope = rememberCoroutineScope()

    BackHandler(true, onBack = {
        if (!addEditDreamState.isDreamExitOff && !addEditDreamState.dreamIsSavingLoading
            && !addEditDreamState.dreamAIImage.isLoading && !addEditDreamState.dreamAIExplanation.isLoading
            && !addEditDreamState.dreamAIAdvice.isLoading && !addEditDreamState.dreamAIStory.isLoading &&
            !addEditDreamState.dreamAIMoodAnalyser.isLoading && !addEditDreamState.dreamAIQuestionAnswer.isLoading
        ) {
            if (addEditDreamState.dreamHasChanged) {
                onAddEditDreamEvent(AddEditDreamEvent.TriggerVibration)
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDialogState(true))
            } else {
                onAddEditDreamEvent(AddEditDreamEvent.TriggerVibration)
                onNavigateToDreamJournalScreen()
            }
        } else {
            //TODO: Show a snackbar that the dream is saving
        }
    })

    val dreamBackgroundImage = remember {
        val initialIndex = when {
            dreamImage >= 0 && dreamImage < Dream.dreamBackgroundImages.size -> {
                // dreamImage is a valid index within the list range
                dreamImage
            }

            addEditDreamState.dreamInfo.dreamBackgroundImage >= 0 &&
                    addEditDreamState.dreamInfo.dreamBackgroundImage < Dream.dreamBackgroundImages.size -> {
                // Fallback to the stored dream background image index if within the valid range
                addEditDreamState.dreamInfo.dreamBackgroundImage
            }

            else -> {
                Dream.dreamBackgroundImages.indices.random()
            }
        }
        mutableIntStateOf(initialIndex)
    }

    Crossfade(
        targetState = dreamBackgroundImage.intValue,
        label = "Background Image Crossfade"
    ) { index ->
        Image(
            painter = painterResource(Dream.dreamBackgroundImages[index]),
            contentDescription = "Dream Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(15.dp)
        )
    }

    if (addEditDreamState.dialogState) {
        focusManager.clearFocus()
        keyboardController?.hide()
        AlertSave(
            onDismiss = {
                onAddEditDreamEvent(AddEditDreamEvent.TriggerVibration)
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDialogState(false))
                scope.launch {
                    onNavigateToDreamJournalScreen()
                }
            },
            onConfirm = {
                onAddEditDreamEvent(AddEditDreamEvent.TriggerVibration)
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDialogState(false))

                onAddEditDreamEvent(AddEditDreamEvent.SaveDream(onSaveSuccess = {
                    onMainEvent(MainScreenEvent.SetDreamRecentlySaved(true))
                    onMainEvent(MainScreenEvent.ShowSnackBar("Dream Saved Successfully :)"))
                    onNavigateToDreamJournalScreen()
                }))
            },
            onClickOutside = {
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDialogState(false))
            }
        )
    }

    val tint = if (!addEditDreamState.dreamIsSavingLoading && !addEditDreamState.isDreamExitOff
    ) White
    else Color.Gray.copy(alpha = 0.1f)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        Text(
                            text = "Dream Journal AI",
                            color = White,
                            style = typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            //if no changes, navigate back
                            if (addEditDreamState.dreamHasChanged) {
                                onAddEditDreamEvent(AddEditDreamEvent.ToggleDialogState(true))
                            } else {
                                onAddEditDreamEvent(AddEditDreamEvent.TriggerVibration)
                                onNavigateToDreamJournalScreen()
                            }
                        },
                        enabled = !addEditDreamState.isDreamExitOff && !addEditDreamState.dreamIsSavingLoading
                    ) {
                        Icon(
                            modifier = Modifier.rotate(180f),
                            imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                            contentDescription = "Back",
                            tint = tint
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            onMainEvent(MainScreenEvent.SetDreamRecentlySaved(true))
                            onAddEditDreamEvent(AddEditDreamEvent.TriggerVibration)
                            onAddEditDreamEvent(AddEditDreamEvent.SaveDream(onSaveSuccess = {
                                keyboardController?.hide()
                                onMainEvent(MainScreenEvent.ShowSnackBar("Dream Saved Successfully :)"))
                                onNavigateToDreamJournalScreen()
                            }))
                        },
                        enabled = !addEditDreamState.dreamIsSavingLoading && !addEditDreamState.isDreamExitOff
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = stringResource(Res.string.save_dream),
                            tint = tint
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = LightBlack.copy(alpha = 0.7f),
                    navigationIconContentColor = Color.Black,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                ),
                modifier = Modifier.dynamicBottomNavigationPadding()
            )
        },
        snackbarHost = {
            SnackbarHost(
                addEditDreamState.snackBarHostState.value, modifier = Modifier
                    .consumeWindowInsets(
                        WindowInsets.navigationBars
                    )
                    .imePadding()
                    .padding(16.dp)
            )
        },
        containerColor = Color.Transparent,
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            TabLayout(
                dreamBackgroundImage = dreamBackgroundImage,
                dreamTitleState = dreamTitleState,
                dreamContentState = dreamContentState,
                addEditDreamState = addEditDreamState,
                onAddEditDreamEvent = onAddEditDreamEvent,
                keyboardController = keyboardController,
                animatedVisibilityScope = animateVisibilityScope,
                onImageClick = onImageClick
            )
        }
    }
}
