package de.rogallab.mobile.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import de.rogallab.mobile.ui.navigation.AppNavHost
import de.rogallab.mobile.ui.theme.AppTheme

class MainActivity : BaseActivity(tag) {
   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      // Start Windows insets
      // https://developer.android.com/develop/ui/compose/layouts/insets
      enableEdgeToEdge()
      // use insets to show to snackbar above ime keyboard
      window?.let {  window ->
         WindowCompat.setDecorFitsSystemWindows(window, false)
      }

      setContent {

         AppTheme {
            Box(
               modifier = Modifier.safeDrawingPadding()
            ){
               AppNavHost()
            }
         }
      }
   }

   companion object {
      const val isInfo = true
      const val isDebug = true
      const val isVerbose  = true
      //12345678901234567890123
      private const val tag = "[MainActivity]"
   }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
   AppTheme {
      AppNavHost()
   }
}