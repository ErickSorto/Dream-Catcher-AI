package org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.components

//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.ui.platform.LocalContext
//import androidx.hilt.navigation.compose.hiltViewModel
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.launch
//import org.ballistic.dreamjournalai.core.Resource
//import org.ballistic.dreamjournalai.dream_main.presentation.ScaffoldState



// TODO: finish this
//@Composable
//fun RevokeAccess(
//    viewModel: AuthViewModel = hiltViewModel(),
//    scaffoldState: ScaffoldState,
//    coroutineScope: CoroutineScope,
//    signOut: () -> Unit,
//) {
//    val context = LocalContext.current
//
//    fun showRevokeAccessMessage() {
//        coroutineScope.launch {
//            val result = scaffoldState.snackbarHostState.showSnackbar(
//                message = REVOKE_ACCESS_MESSAGE,
//                actionLabel = SIGN_OUT
//            )
//            if (result == SnackbarResult.ActionPerformed) {
//                signOut()
//            }
//        }
//    }
//
//    when (val revokeAccessResponse = viewModel.revokeAccessResponse) {
//        is Resource.Loading -> ProgressBar()
//        is Resource.Success -> {
//            val isAccessRevoked = revokeAccessResponse.data
//            LaunchedEffect(isAccessRevoked) {
//                if (isAccessRevoked) {
//                    showMessage(context, ACCESS_REVOKED_MESSAGE)
//                }
//            }
//        }
//        is Resource.Error -> {
//            revokeAccessResponse.exception?.let { e ->
//                LaunchedEffect(e) {
//                    print(e)
//                    if (e.message == SENSITIVE_OPERATION_MESSAGE) {
//                        showRevokeAccessMessage()
//                    }
//                }
//            }
//        }
//    }
//}
