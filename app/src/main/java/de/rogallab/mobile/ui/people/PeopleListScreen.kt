package de.rogallab.mobile.ui.people

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import de.rogallab.mobile.R
import de.rogallab.mobile.domain.model.Person
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.ui.navigation.NavScreen
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleListScreen(
   navController: NavController,
   viewModel: PeopleViewModel,
) {

   val tag = "ok>PeopleListScreen   ."

   Column(
      modifier = Modifier.fillMaxWidth()
   ) {

      TopAppBar(
         title = { Text(text = stringResource(R.string.people_list)) },
         navigationIcon = {
            val activity = LocalContext.current as Activity
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

      LazyColumn(
         modifier = Modifier
            .padding(top = 16.dp)
            .padding(horizontal = 16.dp),
         state = rememberLazyListState()
      ) {
         items(
            items = viewModel.people.toList(),
            key = { person: Person -> person.id }
         ) { person ->

            PersonListItem(
               id = person.id,
               firstName = person.firstName,
               lastName = person.lastName,
               email = person.email,
               phone = person.phone,
               imagePath = person.imagePath ?: "",
               onClick = { id ->
                  logDebug(tag, "Forward Navigation: Item clicked")
                  // Navigate to 'PersonDetail' destination and put 'PeopleList' on the back stack
                  navController.navigate(route = NavScreen.PersonDetail.route + "/$id")
               },
            )
         }
      }
      Row(
         modifier = Modifier.padding(all = 16.dp),
         horizontalArrangement = Arrangement.End
      ) {
         FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.tertiary,
            onClick = {
               // FAB clicked -> InputScreen initialized
               viewModel.clearState()
               logDebug(tag, "Forward Navigation: FAB clicked")
               // Navigate to PersonDetail and put PeopleList on the back stack
               navController.navigate(route = NavScreen.PersonInput.route)
            }
         ) {
            Icon(Icons.Default.Add, "Add a contact")
         }
      }
   }
}

@Composable
fun PersonListItem(
   id: UUID,
   firstName: String,
   lastName: String,
   email: String?,
   phone: String?,
   imagePath: String?,
   onClick: (UUID) -> Unit    // Event ↑  Person
) {
   //12345678901234567890123
   val tag = "ok>PersonListItem     ."

   Column {
      Row(
         verticalAlignment = Alignment.CenterVertically,
         modifier = Modifier
            .clickable {
               logDebug(tag, "Row onClick()")
               onClick(id)  // Event ↑
            }
      ) {
         Column {
            Text(
               text = "$firstName $lastName",
               style = MaterialTheme.typography.bodyLarge,
            )
            email?.let {
               Text(
                  modifier = Modifier.padding(top = 4.dp),
                  text = it,
                  style = MaterialTheme.typography.bodyMedium
               )
            }
            phone?.let {
               Text(
                  text = phone,
                  style = MaterialTheme.typography.bodyMedium,
                  modifier = Modifier
               )
            }
         }
      }
      Divider(modifier = Modifier.padding(vertical = 8.dp))
   }
}