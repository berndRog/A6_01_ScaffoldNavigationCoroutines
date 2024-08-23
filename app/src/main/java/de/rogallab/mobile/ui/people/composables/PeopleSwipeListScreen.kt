package de.rogallab.mobile.ui.features.people.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.rogallab.mobile.R
import de.rogallab.mobile.domain.entities.Person
import de.rogallab.mobile.domain.utilities.as8
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.domain.utilities.logVerbose
import de.rogallab.mobile.ui.composables.SetSwipeBackgroud
import de.rogallab.mobile.ui.errors.showUndo
import de.rogallab.mobile.ui.navigation.NavEvent
import de.rogallab.mobile.ui.people.PeopleViewModel
import de.rogallab.mobile.ui.people.composables.PersonListItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleSwipeListScreen(
   viewModel: PeopleViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
   val tag = "[PeopleListScreen]"
   // observe the peopleUiStateFlow in the ViewModel
   // notify the UI when the state changes
   val peopleUiState by viewModel.peopleUiStateFlow.collectAsStateWithLifecycle()
   // read all people from repository, when the screen is created
   LaunchedEffect(Unit) {
      logVerbose(tag, "readPeople()")
      viewModel.fetchPeople()
   }
   val screenTitle = stringResource(R.string.people_list)
   val snackbarHostState = remember { SnackbarHostState() }
   val coroutineScope = rememberCoroutineScope()

   Column(
      modifier = Modifier
         .fillMaxSize()
         .padding(horizontal = 8.dp)
   ) {
      TopAppBar(title = { Text(screenTitle) })

      Row(
         modifier = Modifier.padding(all = 8.dp),
      ) {
         Spacer(modifier = Modifier.weight(0.8f))

         FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.tertiary,
            onClick = {
               logDebug(tag, "FAB clicked -> Navigate to PersonInputScreen")
            }
         ) {
            Icon(Icons.Default.Add, "Add a contact")
         }
      }
      LazyColumn(
         modifier = Modifier
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp),
      ) {
         items(
            items = peopleUiState.people,
            key = { it: Person -> it.id }
         ) { person ->
            val dismissBoxState: SwipeToDismissBoxState =
               rememberSwipeToDismissBoxState(
                  initialValue = SwipeToDismissBoxValue.Settled,
                  confirmValueChange = {
                     if (it == SwipeToDismissBoxValue.StartToEnd) {
                        logDebug(tag, "navigate to PersonDetail")
                        viewModel.navigateTo(NavEvent.ToPersonDetail(person.id))
                        return@rememberSwipeToDismissBoxState true
                     } else if (it == SwipeToDismissBoxValue.EndToStart) {
                        viewModel.removePerson(person.id)
                        // undo delete
                        val job = showUndo(
                           coroutineScope = coroutineScope,
                           snackbarHostState = snackbarHostState,
                           message = "Wollen Sie die Person wirklich löschen?",
                           t = person,
                           onUndoAction = viewModel::undoRemovePerson
                        )
                        coroutineScope.launch {
                           job.join()
                           logDebug(tag, "Dismiss handled -> OwnersList")
                           viewModel.navigateTo(NavEvent.ToPeopleList)
                           //navController.navigate(NavScreen.OwnersList.route)
                        }
                        return@rememberSwipeToDismissBoxState true
                     } else return@rememberSwipeToDismissBoxState false
                  },
                  positionalThreshold = SwipeToDismissBoxDefaults.positionalThreshold,
               )

            SwipeToDismissBox(
               state = dismissBoxState,
               modifier = Modifier.padding(vertical = 4.dp),
               enableDismissFromStartToEnd = true,
               enableDismissFromEndToStart = true,
               backgroundContent = { SetSwipeBackgroud(dismissBoxState) }
            ) {
               Column(modifier = Modifier.clickable {
                  logDebug(tag, "navigate to DetailScreen ${person.id.as8()}")
                  viewModel.navigateTo(NavEvent.ToPersonDetail(person.id))
               }) {
                  PersonListItem(
                     firstName = person.firstName,
                     lastName = person.lastName,
                     email = person.email,
                     phone = person.phone,
                     imagePath = person.imagePath,
                     onClick = { }
                  )
               }
            }
         }
      }
   }
}
