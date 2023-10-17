package de.rogallab.mobile.ui.people

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import de.rogallab.mobile.R
import de.rogallab.mobile.domain.model.Person
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.domain.utilities.logInfo
import de.rogallab.mobile.ui.navigation.NavScreen
import de.rogallab.mobile.ui.people.composables.InputNameMailPhone
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailScreen(
   id: UUID?,
   navController: NavController,
   viewModel: PeopleViewModel,
) {

   val tag: String = "ok>PersonDetailScreen ."
   logDebug(tag, "Start")
   // viewModel.clear()

   var savedPerson by remember {
      mutableStateOf(Person("", ""))
   }

   // The state in the view model, i.e. the values in the detail dialog
   // may only be read if the detail dialog was started with a click
   // on LazyColumn item in PeopleListScreen.
   // If PersonInputScreen is called again after a restart, the input
   // values in the dialogs should remain unchanged (undeleted)
   if (viewModel.isDetail) {
      viewModel.isDetail = false
      id?.let {
         logDebug(tag, "ReadById()")
//       LaunchedEffect(Unit) {
         viewModel.readById(id)
//       }
         savedPerson = viewModel.getPersonFromState()
      } ?: run {
         // viewModel.onErrorChange("id not found")
      }
   }


   BackHandler(
      enabled = true,
      onBack = {
         logInfo(tag, "Back Navigation (Abort)")
         viewModel.setStateFromPerson(savedPerson, savedPerson.id)
         // Navigate to 'PeopleList' destination and clear the back stack. As a
         // result, no further reverse navigation will be possible."
         navController.navigate(route = NavScreen.PeopleList.route) {
            popUpTo(route = NavScreen.PeopleList.route) {
               inclusive = true
            }
         }
      }
   )

   Column(
      modifier = Modifier
         .fillMaxWidth()
         .verticalScroll(
            state = rememberScrollState(),
            enabled = true,
            reverseScrolling = true
         )
   ) {

      TopAppBar(
         title = { Text(stringResource(R.string.person_detail)) },
         navigationIcon = {
            IconButton(onClick = {
               // update data
               viewModel.update()
               // toDo Errorhandling -> Snackbar
               logInfo(tag, "Reverse Navigation")
               // Navigate to 'PeopleList' destination and clear the back stack. As a
               // result, no further reverse navigation will be possible."
               navController.navigate(route = NavScreen.PeopleList.route) {
                  popUpTo(route = NavScreen.PeopleList.route) { inclusive = true }
               }
            }) {
               Icon(imageVector = Icons.Default.ArrowBack,
                  contentDescription = stringResource(R.string.back))
            }
         }
      )

      InputNameMailPhone(
         firstName = viewModel.firstName,                         // State ↓
         onFirstNameChange = { viewModel.onFirstNameChange(it) }, // Event ↑
         lastName = viewModel.lastName,                           // State ↓
         onLastNameChange = { viewModel.onLastNameChange(it) },   // Event ↑
         email = viewModel.email,                                 // State ↓
         onEmailChange = { viewModel.onEmailChange(it) },         // Event ↑
         phone = viewModel.phone,                                 // State ↓
         onPhoneChange = { viewModel.onPhoneChange(it) }          // Event ↑
      )
   }
}
