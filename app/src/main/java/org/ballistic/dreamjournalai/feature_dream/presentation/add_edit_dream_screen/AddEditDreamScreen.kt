package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen


import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.ballistic.dreamjournalai.R
import org.ballistic.dreamjournalai.feature_dream.domain.model.Dream
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.AlertSave
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.TabLayout
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.viewmodel.AddEditDreamState
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.MainScreenEvent
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel.MainScreenViewModelState

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditDreamScreen(
    dreamImage: Int,
    addEditDreamState: AddEditDreamState,
    mainScreenViewModelState: MainScreenViewModelState,
    onMainEvent: (MainScreenEvent) -> Unit = {},
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit = {},
    onNavigateToDreamJournalScreen: () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    listOf(
        MainScreenEvent.SetBottomBarState(false),
        MainScreenEvent.SetFloatingActionButtonState(false),
        MainScreenEvent.SetSearchingState(false),
        MainScreenEvent.SetDrawerState(false)
    ).forEach(onMainEvent)

    listOf(
        AddEditDreamEvent.GetUnlockedWords,
        AddEditDreamEvent.LoadWords
    ).forEach(onAddEditDreamEvent)

    BackHandler(enabled = !addEditDreamState.isDreamExitOff && !addEditDreamState.dreamIsSavingLoading.value) {
        addEditDreamState.dialogState.value = true
    }

    val dreamBackgroundImage = remember {
        mutableStateOf(
            when {
                dreamImage != -1 && dreamImage in Dream.dreamBackgroundImages -> dreamImage
                dreamImage != -1 -> R.drawable.background_during_day
                else -> addEditDreamState.dreamInfo.dreamBackgroundImage
            }
        )
    }

    Crossfade(targetState = dreamBackgroundImage.value, label = "") { image ->
        Image(
            painter = painterResource(id = image),
            contentDescription = "Dream Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().blur(10.dp)
        )
    }

    if (addEditDreamState.dialogState.value) {
        AlertSave(
            onDismiss = {
                onNavigateToDreamJournalScreen()
                addEditDreamState.dialogState.value = false
            },
            onConfirm = {
                addEditDreamState.dialogState.value = false
                onAddEditDreamEvent(AddEditDreamEvent.SaveDream(onSaveSuccess = {
                    onMainEvent(MainScreenEvent.ShowSnackBar("Dream Saved Successfully :)"))
                    onNavigateToDreamJournalScreen()
                }))
            },
            onClickOutside = {
                addEditDreamState.dialogState.value = false
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Dream Journal AI",
                        color = colorResource(id = R.color.white)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { addEditDreamState.dialogState.value = true },
                        enabled = !addEditDreamState.isDreamExitOff && !addEditDreamState.dreamIsSavingLoading.value
                    ) {
                        Icon(
                            modifier = Modifier.rotate(180f),
                            imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                            contentDescription = "Back",
                            tint = if (!addEditDreamState.dreamIsSavingLoading.value && !addEditDreamState.isDreamExitOff
                            ) colorResource(id = R.color.white)
                            else Color.Gray.copy(alpha = 0.1f)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            keyboardController?.hide()
                            onAddEditDreamEvent(AddEditDreamEvent.SaveDream(onSaveSuccess = {
                                onMainEvent(MainScreenEvent.ShowSnackBar("Dream Saved Successfully :)"))
                                onNavigateToDreamJournalScreen()
                            }))
                        },
                        enabled = !addEditDreamState.dreamIsSavingLoading.value && !addEditDreamState.isDreamExitOff
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = "Save Dream",
                            tint = if (!addEditDreamState.dreamIsSavingLoading.value && !addEditDreamState.isDreamExitOff
                            ) colorResource(
                                id = R.color.white
                            )
                            else Color.Gray.copy(alpha = 0.1f)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorResource(id = R.color.dark_blue).copy(alpha = 0.5f),
                    navigationIconContentColor = Color.Black,
                    titleContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                ),
                modifier = Modifier.padding()
            )
        },
        snackbarHost = { SnackbarHost(addEditDreamState.snackBarHostState.value) },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(padding)
        ) {
            TabLayout(
                dreamBackgroundImage,
                mainScreenViewModelState = mainScreenViewModelState,
                addEditDreamState = addEditDreamState,
                onAddEditDreamEvent = onAddEditDreamEvent
            )
        }
    }
}
