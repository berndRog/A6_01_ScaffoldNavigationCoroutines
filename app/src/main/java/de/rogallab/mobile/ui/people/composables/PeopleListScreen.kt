package de.rogallab.mobile.ui.people.composables

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.rogallab.mobile.R
import de.rogallab.mobile.domain.entities.Person
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.domain.utilities.logInfo
import de.rogallab.mobile.ui.composables.SetSwipeBackgroud
import de.rogallab.mobile.ui.errors.ErrorParams
import de.rogallab.mobile.ui.errors.ErrorUiState
import de.rogallab.mobile.ui.errors.showError
import de.rogallab.mobile.ui.navigation.NavEvent
import de.rogallab.mobile.ui.navigation.NavScreen
import de.rogallab.mobile.ui.people.PeopleViewModel
import de.rogallab.mobile.ui.people.PersonIntent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleListScreen(
   viewModel: PeopleViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
   val tag = "<-PeopleListScreen"

   // Observe the peopleUiState of the viewmodel
   val peopleUiState by viewModel.peopleUiStateFlow.collectAsStateWithLifecycle()

   val activity = LocalContext.current as Activity

   // Back navigation
   BackHandler(
      enabled = true,
      onBack = {  activity.finish() }
   )

   val undoDeletePerson = stringResource(R.string.undoDeletePerson)
   val undoAnswer = stringResource(R.string.undoAnswer)

   val snackbarHostState = remember { SnackbarHostState() }

   val windowInsets = WindowInsets.systemBars
      .add(WindowInsets.safeGestures)

   Scaffold(
      modifier = Modifier
         .fillMaxSize()
         .padding(windowInsets.asPaddingValues())
         .background(color = MaterialTheme.colorScheme.surface),
      topBar = {
         TopAppBar(
            title = { Text(text = stringResource(R.string.people_list)) },
            navigationIcon = {
               IconButton(
                  onClick = {
                     logDebug(tag, "Lateral Navigation: finish app")
                     // Finish the app
                     activity.finish()
                  }
               ) {
                  Icon(imageVector = Icons.Default.Menu,
                     contentDescription = stringResource(R.string.back))
               }
            }
         )
      },
      floatingActionButton = {
         FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.tertiary,
            onClick = {
               // FAB clicked -> InputScreen initialized
               viewModel.onProcessIntent(PersonIntent.Clear)
               logInfo(tag, "Forward Navigation -> PersonInput")
               viewModel.navigate(NavEvent.NavigateForward(NavScreen.PersonInput.route))
            }
         ) {
            Icon(Icons.Default.Add, "Add a contact")
         }
      },
      snackbarHost = {
         SnackbarHost(hostState = snackbarHostState) { data ->
            Snackbar(snackbarData = data, actionOnNewLine = true)
         }
      }
   ) { paddingValues: PaddingValues ->

      LazyColumn(
         modifier = Modifier
            .padding(paddingValues = paddingValues)
            .padding(horizontal = 16.dp)
      ) {
         items(
            items = peopleUiState.people.sortedBy { it.firstName },
            key = { it: Person -> it.id }
         ) { person ->


            var hasNavigated by remember { mutableStateOf(false) }

            val swipeToDismissBoxState: SwipeToDismissBoxState =
               rememberSwipeToDismissBoxState(
                  initialValue = SwipeToDismissBoxValue.Settled,
                  confirmValueChange = { swipe: SwipeToDismissBoxValue ->
                     if (swipe == SwipeToDismissBoxValue.StartToEnd && !hasNavigated) {
                        logDebug(tag, "navigate to PersonDetail")
                        //viewModel.viewModelScope.launch {
                           viewModel.navigate(
                              NavEvent.NavigateForward(NavScreen.PersonDetail.route + "/${person.id}"))
                           hasNavigated = true
                        //}
                        return@rememberSwipeToDismissBoxState true
                     } else if (swipe == SwipeToDismissBoxValue.EndToStart) {
                        viewModel.onProcessIntent(PersonIntent.Remove(person))
                        // undo remove?
                        viewModel.onErrorEvent(
                           params = ErrorParams(
                              message = undoDeletePerson,
                              actionLabel = undoAnswer,
                              duration = SnackbarDuration.Short,
                              withDismissAction = false,
                              onDismissAction = { viewModel.onProcessIntent(PersonIntent.UndoRemove) },
                              navEvent = NavEvent.NavigateForward(route = NavScreen.PeopleList.route)
                           )
                        )
                        return@rememberSwipeToDismissBoxState true
                     } else return@rememberSwipeToDismissBoxState false
                  },
                  positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold,
               )

            SwipeToDismissBox(
               state = swipeToDismissBoxState,
               backgroundContent = { SetSwipeBackgroud(swipeToDismissBoxState) },
               modifier = Modifier.padding(vertical = 4.dp),
               // enable dismiss from start to end (left to right)
               enableDismissFromStartToEnd = true,
               // enable dismiss from end to start (right to left)
               enableDismissFromEndToStart = true
            ) {
               // content
               PersonCard(
                  firstName = person.firstName,
                  lastName = person.lastName,
                  email = person.email,
                  phone = person.phone,
                  imagePath = person.imagePath
               )
            }
         }
      }
   }

   val errorState: ErrorUiState by viewModel.errorUiStateFlow.collectAsStateWithLifecycle()
   LaunchedEffect(errorState.params) {
      errorState.params?.let { params: ErrorParams ->
         logDebug(tag, "ErrorUiState: ${errorState.params}")

         // show the error with a snackbar
         showError(snackbarHostState, params, viewModel::navigate )

         // reset the errorState, params are copied to showError
         viewModel.onErrorEventHandled()

      }
   }
}
