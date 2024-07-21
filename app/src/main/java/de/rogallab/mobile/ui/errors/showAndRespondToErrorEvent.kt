package de.rogallab.mobile.ui.errors

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import de.rogallab.mobile.ui.navigation.NavEvent

suspend fun showAndRespondToError(
   params: ErrorParams,
   onErrorEventHandled: () -> Unit,
   onNavigateTo: (NavEvent) -> Unit,
   snackbarHostState: SnackbarHostState,
) {
   // show snackbar
   val result = snackbarHostState.showSnackbar(
      message = params.message,
      actionLabel = params.actionLabel,
      duration = params.duration,
      withDismissAction = params.withDismissAction,
   )
   // action on dismiss
   if (params.withDismissAction &&
      result == SnackbarResult.ActionPerformed
   ) {
      params.onDismissAction()
   }

   // if navigation is true, nevaigate to route
   params.navEvent?.let { event ->
     onNavigateTo(event)
   }

   // set ErrorEvent to handled
   onErrorEventHandled()
}
