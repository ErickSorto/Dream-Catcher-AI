package org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen


import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
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
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.AlertSave
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.components.TabLayout
import org.ballistic.dreamjournalai.feature_dream.presentation.add_edit_dream_screen.viewmodel.AddEditDreamState
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.MainScreenEvent
import org.ballistic.dreamjournalai.feature_dream.presentation.main_screen.viewmodel.MainScreenViewModelState

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AddEditDreamScreen(
    dreamImage: Int,
    addEditDreamState: AddEditDreamState,
    mainScreenViewModelState: MainScreenViewModelState,
    onMainEvent: (MainScreenEvent) -> Unit = {},
    onAddEditDreamEvent: (AddEditDreamEvent) -> Unit = {},
    onNavigateToDreamJournalScreen: () -> Unit = {},
) {
    //keyboard controller
    val keyboardController = LocalSoftwareKeyboardController.current

    onMainEvent(MainScreenEvent.SetBottomBarState(false))
    onMainEvent(MainScreenEvent.SetFloatingActionButtonState(false))
    onMainEvent(MainScreenEvent.SetTopBarState(false))
    onMainEvent(MainScreenEvent.SetSearchingState(false))

    BackHandler {
        if (!addEditDreamState.dreamIsSavingLoading.value) {
            addEditDreamState.dialogState.value = !addEditDreamState.dialogState.value
        }
    }

    val dreamBackgroundImage = remember {
        mutableStateOf(
            if (dreamImage != -1) dreamImage else addEditDreamState.dreamInfo.dreamBackgroundImage
        )
    }

    Crossfade(targetState = dreamBackgroundImage.value) { image ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding()
        ) {
            Image(
                painter = painterResource(id = image),
                contentDescription = "Dream Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .blur(10.dp)
            )
        }
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
                title = { Text(text = "Dream Journal AI", color = colorResource(id = R.color.white)) },
                navigationIcon = {
                    IconButton(
                        onClick = { addEditDreamState.dialogState.value = true },
                        enabled = !addEditDreamState.dreamIsSavingLoading.value
                    ) {
                        Icon(
                            modifier = Modifier.rotate(180f),
                            imageVector = Icons.Filled.ArrowRightAlt,
                            contentDescription = "Back",
                            tint = colorResource(id = R.color.white)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        keyboardController?.hide()
                        onAddEditDreamEvent(AddEditDreamEvent.SaveDream(onSaveSuccess = {
                            onMainEvent(MainScreenEvent.ShowSnackBar("Dream Saved Successfully :)"))
                            onNavigateToDreamJournalScreen()
                        }))
                    }, enabled = !addEditDreamState.dreamIsSavingLoading.value) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = "Save Dream",
                            tint = colorResource(id = R.color.white)
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
        snackbarHost = {
            SnackbarHost(addEditDreamState.snackBarHostState.value)
        },
        containerColor = Color.Transparent,

        ) { padding ->
        //change bottom to 0.dp in padding

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