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

   val tag = "ok>PersonDetailScreen ."

   var savedPerson by remember {
      mutableStateOf(Person("", ""))
   }

   id?.let {
      LaunchedEffect(true) {
         logDebug(tag, "ReadById()")
         viewModel.readById(id)
     }
     savedPerson = viewModel.getPersonFromState()
   }

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
         title = { Text(stringResource(R.string.person_detail)) },
         navigationIcon = {
            IconButton(onClick = {
               logDebug(tag, "Up (reverse) navigation: viewModel.update()")
               viewModel.update()
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
         onFirstNameChange = viewModel::onFirstNameChange,        // Event ↑
         lastName = viewModel.lastName,                           // State ↓
         onLastNameChange = viewModel::onLastNameChange,          // Event ↑
         email = viewModel.email,                                 // State ↓
         onEmailChange = viewModel::onEmailChange,                // Event ↑
         phone = viewModel.phone,                                 // State ↓
         onPhoneChange = viewModel::onPhoneChange,                // Event ↑
      )
   }
}
