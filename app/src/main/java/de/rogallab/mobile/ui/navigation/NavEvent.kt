package de.rogallab.mobile.ui.navigation

import java.util.UUID

sealed interface NavEvent {
   data object ToPersonInput : NavEvent
   data class  ToPersonDetail(val id: UUID) : NavEvent
   data object ToPeopleList : NavEvent

   data object NavigateBack : NavEvent
   data class NavigateTo(val route: String) : NavEvent
   data class NavigateToAndClearBackStack(val route: String) : NavEvent
}
