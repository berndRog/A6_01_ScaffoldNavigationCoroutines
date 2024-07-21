package de.rogallab.mobile.ui.navigation
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.rogallab.mobile.domain.utilities.logVerbose
import de.rogallab.mobile.ui.people.PeopleListScreen
import de.rogallab.mobile.ui.people.PeopleViewModel
import de.rogallab.mobile.ui.people.PersonScreen
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

@Composable
fun AppNavHost(
   // Injecting the ViewModel by koin() is recommended by Gemini
   peopleViewModel: PeopleViewModel = koinViewModel()
) {
  val tag ="[AppNavHost]"

   val navController: NavHostController = rememberNavController()
   val duration = 800  // in

   // One time event to navigate to the start destination
   ObserveAsEvents(peopleViewModel.navigationChannelFlow) { event: NavEvent ->
      logVerbose(tag, "event: $event")
      when (event) {
         is NavEvent.ToPeopleList ->
            navController.navigate(NavScreen.PeopleList.route)
         is NavEvent.ToPersonInput ->
            navController.navigate(NavScreen.PersonInput.route)
         is NavEvent.ToPersonDetail ->
            navController.navigate(NavScreen.PersonDetail.route+"/"+event.id.toString())

         // Returns to the previous screen and pops the current screen off the back stack.
         is NavEvent.NavigateBack -> navController.navigateUp()

         // Navigates to destination and clears the back stack up to the specified destination.
         is NavEvent.NavigateTo ->  navController.navigate(event.route) {
            popUpTo(event.route) { inclusive = true } }

         // Navigates to the destination route and clears the back stack up to the start screen.
         is NavEvent.NavigateToAndClearBackStack -> navController.navigate(event.route) {
            popUpTo(navController.graph.startDestinationId) { inclusive = true }
         }

         else -> Unit
      }
   }


   NavHost(
      navController = navController,
      startDestination = NavScreen.PeopleList.route,
      enterTransition = { enterTransition(duration) },
      exitTransition  = { exitTransition(duration)  },
      popEnterTransition = { popEnterTransition(duration)},
      popExitTransition = { popExitTransition(duration) }
   ) {
      composable(
         route = NavScreen.PeopleList.route,
      ) {
         PeopleListScreen(
            navController = navController,
            viewModel = peopleViewModel
         )
      }

      composable(
         route = NavScreen.PersonInput.route,
      ) {
         PersonScreen(
            isInputScreen = true,
            id = null
         )
      }

      composable(
         route = NavScreen.PersonDetail.route + "/{personId}",
         arguments = listOf(navArgument("personId") { type = NavType.StringType}),
      ) { backStackEntry ->
         val id = backStackEntry.arguments?.getString("personId")?.let{
            UUID.fromString(it)
         }
         PersonScreen(
            isInputScreen = false,
            id = id
         )
      }
   }
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
) = fadeIn(animationSpec = tween(duration)) + slideIntoContainer(
   animationSpec = tween(duration),
   towards = AnimatedContentTransitionScope.SlideDirection.Up
)

private fun AnimatedContentTransitionScope<NavBackStackEntry>.popExitTransition(
   duration: Int
) = fadeOut(animationSpec = tween(duration))
