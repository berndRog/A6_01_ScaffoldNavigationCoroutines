package de.rogallab.mobile.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import de.rogallab.mobile.ui.base.BaseActivity
import de.rogallab.mobile.ui.navigation.composables.AppNavHost
import de.rogallab.mobile.ui.people.PersonViewModel
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
            val viewModel: PersonViewModel = koinViewModel()
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
