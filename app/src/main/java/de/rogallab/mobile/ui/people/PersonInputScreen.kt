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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import de.rogallab.mobile.R
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.domain.utilities.logInfo
import de.rogallab.mobile.ui.navigation.NavScreen
import de.rogallab.mobile.ui.people.composables.InputNameMailPhone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonInputScreen(
   navController: NavController,
   viewModel: PeopleViewModel
) {
   val tag = "ok>PersonInputScreen  ."

   // The state in the view model, i.e. the values in the input dialogs
   // may only be deleted if the input dialog was started with the FAB
   // in PeopleListScreen.
   // If PersonInputScreen is called again after a restart, the input
   // values in the dialogs should remain unchanged (undeleted)
   if (viewModel.isInput) {
      viewModel.isInput = false
      viewModel.clearState()
   }

   BackHandler(
      enabled = true,
      onBack = {
         logInfo(tag, "Back Navigation (Abort)")
         viewModel.clearState()
         // Navigate to 'PeopleList' destination and clear the back stack. As a
         // result, no further reverse navigation will be possible."
         navController.navigate(
            route = NavScreen.PeopleList.route
         ) {
            popUpTo(route = NavScreen.PeopleList.route) {
               inclusive = true
            }
         }
      }
   )

   // https://dev.to/tkuenneth/keyboard-handling-in-jetpack-compose-2593

   Column(
      modifier = Modifier
         .fillMaxWidth()
         .verticalScroll(state = rememberScrollState())
   ) {

      TopAppBar(
         title = { Text(stringResource(R.string.person_input)) },
         navigationIcon = {
            IconButton(
               onClick = {
                  logDebug(tag, "Up (reverse) navigation + viewModel.add()")
                  val id = viewModel.add()
                  // navigate ...
                  // Navigate to 'PeopleList' destination and clear the back stack. As a
                  // result, no further reverse navigation will be possible."
                  navController.navigate(route = NavScreen.PeopleList.route) {
                     popUpTo(route = NavScreen.PeopleList.route) {
                        inclusive = true
                     }
                  }
               }) {
               Icon(
                  imageVector = Icons.Default.ArrowBack,
                  contentDescription = stringResource(R.string.back)
               )
            }
         }
      )

      InputNameMailPhone(
         firstName = viewModel.firstName,                         // State ↓
         onFirstNameChange = viewModel::onFirstNameChange, // Event ↑
         lastName = viewModel.lastName,                           // State ↓
         // instead of using a function inside a lambda
         // a function reference can be used
         onLastNameChange = viewModel::onLastNameChange,          // Event ↑
         email = viewModel.email,                                 // State ↓
         onEmailChange = viewModel::onEmailChange,                // Event ↑
         phone = viewModel.phone,                                 // State ↓
         onPhoneChange = viewModel::onPhoneChange,                // Event ↑
      )
   }
}