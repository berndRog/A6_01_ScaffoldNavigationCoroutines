package de.rogallab.mobile.ui.navigation

// navigation targets
sealed interface NavEvent {
   data object ToPersonInput : NavEvent
   data class  ToPersonDetail(val id: String) : NavEvent
   data object ToPeopleList : NavEvent

   data object NavigateBack : NavEvent
   data class NavigateTo(val route: String) : NavEvent
}
