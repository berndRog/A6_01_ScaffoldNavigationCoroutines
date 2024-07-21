package de.rogallab.mobile.ui.people.base

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.domain.utilities.logError
import de.rogallab.mobile.ui.errors.ErrorParams
import de.rogallab.mobile.ui.errors.ErrorState
import de.rogallab.mobile.ui.navigation.NavEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

open class BaseViewModel(
   private val _tag: String
) : ViewModel() {

// Navigation State = ViewModel (one time) UI event
//   private var _navState: MutableState<NavState> =
//      mutableStateOf(NavState(onNavRequestHandled = ::onNavEventHandled))
//   val navState: NavState
//      get() = _navState.value
//   fun onNavEvent(route: String, clearBackStack: Boolean = true) {
//      _navState.value = navState.copy(route = route, clearBackStack = clearBackStack)
//   }
//   fun onNavEventHandled() {
//      _navState.value = navState.copy(route = null, clearBackStack = true )
//   }
   protected val navigationChannel: Channel<NavEvent> = Channel(Channel.BUFFERED)
   val navigationChannelFlow = navigationChannel.receiveAsFlow()

   fun onNavigateTo(event: NavEvent) {
      viewModelScope.launch {
         navigationChannel.send(event)
      }
   }

   // Error  State = ViewModel (one time) events
   // https://developer.android.com/topic/architecture/ui-layer/events#handle-viewmodel-events
   private val _errorState: MutableState<ErrorState> = mutableStateOf(ErrorState())
   val errorStateValue: ErrorState
      get() = _errorState.value

   fun showOnFailure(throwable: Throwable, navEvent: NavEvent? = null) {
      when (throwable) {
         is CancellationException -> {
            val error = throwable.localizedMessage ?: "Cancellation error"
            showOnError(error, null)
         }
//         is RedirectResponseException -> {
//            val error = "Redirect error: ${throwable.response.status.description}"
//            showOnError(error, navEvent)
//         }
//         is ClientRequestException -> {
//            val error = "Client error: ${throwable.response.status.description}"
//            showOnError(error, navEvent)
//         }
//         is ServerResponseException -> {
//            val error = "Server error: ${throwable.response.status.description}"
//            showOnError(error, navEvent)
//         }
//         is ConnectTimeoutException -> showOnError("Connect timed out", navEvent)
//         is SocketTimeoutException -> showOnError("Socket timed out", navEvent)
//         is UnknownHostException -> showOnError("No internet connection", navEvent)
         else ->
            showOnError(throwable.localizedMessage ?: "Unknown error", navEvent)
      }
   }

   fun showOnError(message: String, navEvent: NavEvent?) {
      logError(_tag, message)
      _errorState.value = _errorState.value.copy(
         params = ErrorParams(message = message, navEvent = navEvent)
      )
   }

   fun onErrorEventHandled() {
      logDebug(_tag, "onErrorEventHandled()")
      _errorState.value = _errorState.value.copy(params = null)
   }
}