package de.rogallab.mobile.ui.navigation
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.rogallab.mobile.domain.utilities.logInfo
import de.rogallab.mobile.ui.people.PeopleViewModel
import de.rogallab.mobile.ui.people.composables.PeopleListScreen
import de.rogallab.mobile.ui.people.composables.PersonScreen

@Composable
fun AppNavHost(
   // create a NavHostController with a factory function
   navController: NavHostController = rememberNavController(),
   // Injecting the ViewModel by koin()
   peopleViewModel: PeopleViewModel = viewModel()
) {
   val tag = "[AppNavHost]"
   val duration = 1000  // in milliseconds

   NavHost(
      navController = navController,
      startDestination = NavScreen.PeopleList.route,
      enterTransition = { enterTransition(duration) },
      exitTransition  = { exitTransition(duration)  },
     // popEnterTransition = { popEnterTransition(duration) },
     // popExitTransition = { popExitTransition(duration) }
   ) {
      composable( route = NavScreen.PeopleList.route ) {
         PeopleListScreen(
            viewModel = peopleViewModel
         )
      }

      composable( route = NavScreen.PersonInput.route ) {
         PersonScreen(
            viewModel = peopleViewModel,
            isInputScreen = true
         )
      }

      composable(
         route = NavScreen.PersonDetail.route + "/{personId}",
         arguments = listOf(navArgument("personId") { type = NavType.StringType}),
      ) { backStackEntry ->
         val id = backStackEntry.arguments?.getString("personId")
         PersonScreen(
            viewModel = peopleViewModel,
            isInputScreen = false,
            id = id
         )
      }
   }

   // Observing the navigation state and handle navigation
   val navigationState: NavUiState by peopleViewModel.navUiStateFlow.collectAsStateWithLifecycle()
   navigationState.event?.let { navEvent: NavEvent ->
      logInfo(tag, "navigation event: $navEvent")
      when(navEvent) {
         is NavEvent.ToPeopleList -> {
            // delete the current screen from back stack
            navController.popBackStack()
            // navigate to the PeopleListScreen
            navController.navigate(NavScreen.PeopleList.route)
            // reset the NavState to null, i.e. navigation event is handled
            peopleViewModel.onNavEventHandled()
         }
         is NavEvent.ToPersonInput -> {
            navController.popBackStack()
            navController.navigate(NavScreen.PersonInput.route)
            peopleViewModel.onNavEventHandled()
         }
         is NavEvent.ToPersonDetail -> {
            navController.popBackStack()
            navController.navigate(NavScreen.PersonDetail.route + "/" + navEvent.id)
            peopleViewModel.onNavEventHandled()
         }

         is NavEvent.NavigateBack -> {
            navController.navigateUp()
            peopleViewModel.onNavEventHandled()
         }
         is NavEvent.NavigateTo -> {
            navController.navigate(navEvent.route) {
               popUpTo(navEvent.route) { inclusive = true }
            }
            peopleViewModel.onNavEventHandled()
         }
      }
   }

}

fun navigateAndPopCurrentDestination(
   navController: NavController,
   route: String,
   onNavEventHandled: () -> Unit
) {
   navController.popBackStack()  // Pops the current destination off the back stack
   navController.navigate(route) // Navigates to the new destination
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.enterTransition(
   duration: Int
) = fadeIn(animationSpec = tween(duration)) + slideIntoContainer(
      animationSpec = tween(duration),
      towards = AnimatedContentTransitionScope.SlideDirection.Left
   )

private fun AnimatedContentTransitionScope<NavBackStackEntry>.exitTransition(
   duration: Int
) = fadeOut(animationSpec = tween(duration)) + slideOutOfContainer(
         animationSpec = tween(duration),
         towards = AnimatedContentTransitionScope.SlideDirection.Left
      )

private fun AnimatedContentTransitionScope<NavBackStackEntry>.popEnterTransition(
   duration: Int
) = fadeIn(animationSpec = tween(duration))+ slideIntoContainer(
   animationSpec = tween(duration),
   towards = AnimatedContentTransitionScope.SlideDirection.Down
)

private fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition(
   duration: Int
) = fadeOut(animationSpec = tween(duration)) + slideOutOfContainer(
   animationSpec = tween(duration),
   towards = AnimatedContentTransitionScope.SlideDirection.Down
)
