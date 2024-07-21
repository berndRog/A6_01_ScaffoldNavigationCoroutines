package de.rogallab.mobile.ui.people

import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.rogallab.mobile.R
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.ui.errors.ErrorParams
import de.rogallab.mobile.ui.navigation.NavEvent
import de.rogallab.mobile.ui.people.composables.InputEmail
import de.rogallab.mobile.ui.people.composables.InputName
import de.rogallab.mobile.ui.people.composables.InputPhone
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonScreen(
   isInputScreen: Boolean,
   id: UUID?,
   // Injecting the ViewModel by koin() is recommended by Gemini
   peopleViewModel: PeopleViewModel = koinViewModel()
) {
   // Observe the state of the viewmodel
   val personUiState: PersonUiState
      by peopleViewModel.personUiStateFlow.collectAsStateWithLifecycle()
   val isInputMode: Boolean by rememberSaveable { mutableStateOf(isInputScreen) }

   val screenTitle =
      if (isInputMode) stringResource(R.string.person_input)
      else stringResource(R.string.person_detail)
   val tag =
      if (isInputMode) "[PersonInputScreen]"
      else "[PersonDetailScreen]"
   // DetailScreen
   if (!isInputMode) {
      id?.let {
         LaunchedEffect(Unit) {
            peopleViewModel.getById(id)
         }
      } ?: run {
         peopleViewModel.showOnError("No id for person is given", NavEvent.NavigateBack)
      }
   }

   val context = LocalContext.current
   val view = LocalView.current

   BackHandler(
      enabled = true,
      onBack = {
         peopleViewModel.onNavigateTo(NavEvent.NavigateBack)
      }
   )

   Column(
      modifier = Modifier
         .fillMaxWidth()
         //.verticalScroll(state = rememberScrollState())
         .imePadding() // padding for the bottom for the IME
      //.imeNestedScroll()
   ) {
      TopAppBar(
         title = { Text(stringResource(R.string.person_detail)) },
         navigationIcon = {
            IconButton(onClick = {
               logDebug(tag, "Up (reverse) navigation: viewModel.update()")
               // Check input fields and navigate to owners list or show error
               peopleViewModel.validate(isInputMode)
            }) {
               Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                  contentDescription = stringResource(R.string.back))
            }
         }
      )

      InputName(
         name = personUiState.person.firstName,           // State ↓
         onNameChange = peopleViewModel::onFirstNameChange,    // Event ↑
         label = stringResource(R.string.firstName)               // State ↓
      )
      InputName(
         name = personUiState.person.lastName,             // State ↓
         onNameChange = peopleViewModel::onLastNameChange,    // Event ↑
         label = stringResource(R.string.lastName),   // State ↓
      )
      InputEmail(
         email = personUiState.person.email,               // State ↓
         onEmailChange = peopleViewModel::onEmailChange    // Event ↑
      )
      InputPhone(
         phone = personUiState.person.phone,               // State ↓
         onPhoneChange = peopleViewModel::onPhoneChange    // Event ↑
      )
   }

   peopleViewModel.errorStateValue.params?.let { params: ErrorParams ->
      LaunchedEffect(params) {
         // close the keyboard
         val ime = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
         ime.hideSoftInputFromWindow(view.windowToken, 0)

         val message = "!!! --- "+params.message+" ---!!!"

         Toast.makeText(
            context,
            message,
            Toast.LENGTH_LONG
         ).show()

         // if navigation is true, nevaigate to route
         params.navEvent?.let { event ->
            peopleViewModel.onNavigateTo(event)
         }

         // set ErrorEvent to handled
         peopleViewModel.onErrorEventHandled()
      }
   }
}

