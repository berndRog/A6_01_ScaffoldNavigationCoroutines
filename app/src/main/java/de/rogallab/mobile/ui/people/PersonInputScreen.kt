package de.rogallab.mobile.ui.people

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import de.rogallab.mobile.domain.utilities.logInfo
import de.rogallab.mobile.ui.navigation.NavScreen
import de.rogallab.mobile.R
import de.rogallab.mobile.ui.people.composables.InputNameMailPhone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonInputScreen(
   navController: NavController,
   viewModel: PeopleViewModel
) {

   val tag = "ok>PersonInputScreen  ."

   LaunchedEffect(Unit) {
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
            route = NavScreen.PeopleList.route) {
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
         title = { Text(stringResource(R.string.person_input)) },
         navigationIcon = {
            IconButton(
               onClick = {
                  // save data
                  viewModel.add()
                  // toDo errorhandling -> Snackbar
                  logInfo(tag, "Reverse Navigation")
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
         firstName = viewModel.firstName,                          // State ↓
         onFirstNameChange = { viewModel.onFirstNameChange(it) },  // Event ↑
         lastName = viewModel.lastName,                            // State ↓
         onLastNameChange = { viewModel.onLastNameChange(it) },    // Event ↑
         email = viewModel.email,                                  // State ↓
         onEmailChange = { viewModel.onEmailChange(it) },          // Event ↑
         phone = viewModel.phone,                                  // State ↓
         onPhoneChange = { viewModel.onPhoneChange(it) }           // Event ↑
      )
   }
}