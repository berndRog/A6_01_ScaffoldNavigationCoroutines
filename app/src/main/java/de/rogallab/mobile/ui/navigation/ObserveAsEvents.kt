package de.rogallab.mobile.ui.navigation

// Phillip Lackner - https://www.youtube.com/watch?v=njchj9d_Lf8
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Composable
// ObserveAsEvents is a composable function that collects a flow and calls onEvent for each event.
fun <T> ObserveAsEvents(
   flow: Flow<T>,
   onEvent: (T) -> Unit
) {
   val lifecycleOwner = LocalLifecycleOwner.current
   // logVerbose("--ObserveAsEvents", "lifecycleOwner: ${lifecycleOwner.toString()} flow ${flow.toString()}")

   LaunchedEffect(flow, lifecycleOwner.lifecycle) {

      // lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED): This ensures that the collection
      // of events from the Flow only happens when the lifecycle of the composable is in the
      // STARTED state or beyond (e.g., RESUMED).
      lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

         // withContext(Dispatchers.Main.immediate): Switches the coroutine context to the
         // main thread (immediate dispatcher) to ensure that the onEvent callback is executed
         // on the UI thread, as it likely interacts with UI elements.
         withContext(Dispatchers.Main.immediate) {
            flow.collect { event ->
               //logVerbose("--ObserveAsEvents", "onEvent $event")
               onEvent(event)
            }
         }
      }
   }
}
