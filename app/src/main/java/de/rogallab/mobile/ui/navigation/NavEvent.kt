package de.rogallab.mobile.ui.navigation

sealed interface NavEvent {
   // vertical navigation
   data class NavigateForward(val route: String) : NavEvent
   data class NavigateReverse(val route: String) : NavEvent
   data class NavigateBack(val route: String) : NavEvent
}
