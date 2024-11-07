package de.rogallab.mobile.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.rogallab.mobile.ui.IErrorHandler
import de.rogallab.mobile.ui.INavigationHandler
import de.rogallab.mobile.ui.errors.ErrorHandler
import de.rogallab.mobile.ui.errors.ErrorParams
import de.rogallab.mobile.ui.errors.ErrorState
import de.rogallab.mobile.ui.navigation.NavEvent
import de.rogallab.mobile.ui.navigation.NavState
import de.rogallab.mobile.ui.navigation.NavigationHandler
import kotlinx.coroutines.flow.StateFlow

open class BaseViewModel(
   private val _tag: String = "<-BaseViewModel"
): ViewModel(),
   IErrorHandler,
   INavigationHandler {

   // D e l e g a t e   t o   E r r o r H a n d l e r
   private val _errorHandler: IErrorHandler =
      ErrorHandler(viewModelScope, _tag)

   override val errorStateFlow: StateFlow<ErrorState>
      get() = _errorHandler.errorStateFlow

   override fun onErrorEvent(params: ErrorParams) =
      _errorHandler.onErrorEvent(params)

   override fun onErrorEventHandled() =
      _errorHandler.onErrorEventHandled()

   // D e l e g a t e  t o  E r r o r H a n d  l e r
   private val _navHandler: INavigationHandler =
      NavigationHandler(viewModelScope, _tag)

   override val navStateFlow: StateFlow<NavState>
      get() = _navHandler.navStateFlow

   override fun onNavigate(navEvent: NavEvent) =
      _navHandler.onNavigate(navEvent)

   override fun onNavEventHandled() =
      _navHandler.onNavEventHandled()
}