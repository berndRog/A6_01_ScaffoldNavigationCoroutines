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
import de.rogallab.mobile.ui.base.BaseActivity
import de.rogallab.mobile.ui.navigation.AppNavHost
import de.rogallab.mobile.ui.people.PeopleViewModel
import de.rogallab.mobile.ui.theme.AppTheme

class MainActivity : BaseActivity(TAG) {

   private val viewModel: PeopleViewModel by viewModels {
      PeopleViewModelFactory(application)
   }


   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)


      setContent {

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

   companion object {
      const val ISINFO = true
      const val ISDEBUG = true
      const val ISVERBOSE = true
      private const val TAG = "<-MainActivity"
   }
}


class PeopleViewModelFactory(
   private val application: Application
) : ViewModelProvider.Factory {
   override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(PeopleViewModel::class.java)) {
         @Suppress("UNCHECKED_CAST")
         return PeopleViewModel(application) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
   }
}