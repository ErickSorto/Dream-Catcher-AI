package org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen


import android.os.Vibrator
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.core.components.dynamicBottomNavigationPadding
import org.ballistic.dreamjournalai.core.util.VibrationUtils.triggerVibration
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.components.AlertSave
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.components.TabLayout
import org.ballistic.dreamjournalai.dream_add_edit.domain.AddEditDreamEvent
import org.ballistic.dreamjournalai.dream_add_edit.add_edit_dream_screen.viewmodel.AddEditDreamState
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.MainScreenEvent


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AddEditDreamScreen(
    dreamImage: Int,
    dreamTitleState: TextFieldState,
    dreamContentState: TextFieldState,
    addEditDreamState: AddEditDreamState,
    onMainEvent: (MainScreenEvent) -> Unit = {},
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit = {},
    onNavigateToDreamJournalScreen: () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val vibrator = context.getSystemService(Vibrator::class.java)
    val androidVersion = android.os.Build.VERSION.SDK_INT
    val topBarModifier = Modifier
    if (androidVersion != 28) {
        topBarModifier.height(72.dp)
    }
    listOf(
        MainScreenEvent.SetBottomBarVisibilityState(false),
        MainScreenEvent.SetFloatingActionButtonState(false),
        MainScreenEvent.SetSearchingState(false),
        MainScreenEvent.SetDrawerState(false)
    ).forEach(onMainEvent)

    listOf(
        AddEditDreamEvent.GetUnlockedWords,
        AddEditDreamEvent.LoadWords
    ).forEach(onAddEditDreamEvent)

    val scope = rememberCoroutineScope()

    BackHandler(
        enabled = !addEditDreamState.isDreamExitOff && !addEditDreamState.dreamIsSavingLoading
                && !addEditDreamState.dreamAIImage.isLoading && !addEditDreamState.dreamAIExplanation.isLoading
                && !addEditDreamState.dreamAIAdvice.isLoading && !addEditDreamState.dreamAIStory.isLoading &&
                !addEditDreamState.dreamAIMoodAnalyser.isLoading && !addEditDreamState.dreamAIQuestionAnswer.isLoading
    ) {
        onAddEditDreamEvent(AddEditDreamEvent.ToggleDialogState(true))
    }

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
            painter = painterResource(id = Dream.dreamBackgroundImages[index]),
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
                onAddEditDreamEvent(AddEditDreamEvent.ToggleDialogState(false))
                scope.launch {
                    onNavigateToDreamJournalScreen()
                }
            },
            onConfirm = {
                triggerVibration(vibrator)
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
    ) colorResource(id = R.color.white)
    else Color.Gray.copy(alpha = 0.1f)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxHeight()
                    ){
                        Text(
                            text = "Dream Journal AI",
                            color = colorResource(id = R.color.white),
                            style = typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center),
                        )
                    }

                },
                navigationIcon = {
                    IconButton(
                        onClick = { onAddEditDreamEvent(AddEditDreamEvent.ToggleDialogState(true)) },
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
                            triggerVibration(vibrator)
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
                            contentDescription = "Save Dream",
                            tint = tint
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorResource(id = R.color.light_black).copy(alpha = 0.7f),
                    navigationIconContentColor = Color.Black,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                ),
                modifier = topBarModifier.dynamicBottomNavigationPadding()
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
                dreamBackgroundImage,
                dreamTitleState = dreamTitleState,
                dreamContentState = dreamContentState,
                addEditDreamState = addEditDreamState,
                onAddEditDreamEvent = onAddEditDreamEvent,
                keyboardController = keyboardController
            )
        }
    }
}
