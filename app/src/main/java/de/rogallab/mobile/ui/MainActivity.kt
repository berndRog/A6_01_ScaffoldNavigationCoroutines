package de.rogallab.mobile.ui

import android.app.Application
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import de.rogallab.mobile.ui.base.BaseActivity
import de.rogallab.mobile.ui.navigation.composables.AppNavHost
import de.rogallab.mobile.ui.people.PeopleViewModel
import de.rogallab.mobile.ui.people.PersonValidator
import de.rogallab.mobile.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinContext
import org.koin.compose.koinInject

class MainActivity : BaseActivity(TAG) {

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      setContent {

         KoinContext {
            val viewModel: PeopleViewModel = koinViewModel()
            val validator: PersonValidator = koinInject()

            AppTheme {
               Surface(
                  modifier = Modifier.fillMaxSize()
               ) {
                  AppNavHost(
                     peopleViewModel = viewModel //viewModel()
                  )
               }
            }
         }
      }
   }

   companion object {
      private const val TAG = "<-MainActivity"
   }
}
