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

   BackHandler(
      enabled = true,
      onBack = {
         logInfo(tag, "Back Navigation (Abort)")
         navController.popBackStack(
            route = NavScreen.PeopleList.route,
            inclusive = false
         )
      }
   )

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
                  viewModel.add()
                  // Navigate to PeopleList and clear the back stack. As a
                  // result, no further reverse navigation will be possible
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
         onFirstNameChange = { viewModel.onFirstNameChange(it) }, // Event ↑
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