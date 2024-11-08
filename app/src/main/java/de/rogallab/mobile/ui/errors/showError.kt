package de.rogallab.mobile.ui.errors

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import de.rogallab.mobile.ui.navigation.NavEvent

suspend fun showError(
   snackbarHostState: SnackbarHostState,  // State ↓
   params: ErrorParams,                   // State ↓
   onNavigate: (NavEvent) -> Unit,        // Event ↑
) {
   // Show Snackbar
   snackbarHostState.showSnackbar(
      message = params.throwable?.message ?: params.message,
      actionLabel = params.actionLabel,
      withDismissAction = params.withUndoAction,
      duration = params.duration
   ).also { snackbarResult: SnackbarResult ->
   // Handle Snackbar action
      if (snackbarResult == SnackbarResult.ActionPerformed) {
         params.onUndoAction()
      }
   }

   // navigate to target
   params.navEvent?.let { navEvent ->
      onNavigate(navEvent)
   }
}