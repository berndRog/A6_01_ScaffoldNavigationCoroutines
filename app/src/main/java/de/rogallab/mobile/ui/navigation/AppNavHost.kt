package de.rogallab.mobile.ui.navigation
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.ui.people.PeopleListScreen
import de.rogallab.mobile.ui.people.PeopleViewModel
import de.rogallab.mobile.ui.people.PersonDetailScreen
import de.rogallab.mobile.ui.people.PersonInputScreen
import java.util.UUID

@Composable
fun AppNavHost(
   peopleViewModel: PeopleViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
) {
   val tag ="ok>AppNavHost         ."

   val navHostController: NavHostController = rememberNavController()
   val timeSpan  = tween<IntOffset>(2000)
   val left = AnimatedContentTransitionScope.SlideDirection.Left
   val right = AnimatedContentTransitionScope.SlideDirection.Right
   val down = AnimatedContentTransitionScope.SlideDirection.Down
   val up = AnimatedContentTransitionScope.SlideDirection.Up

   NavHost(
      navController = navHostController,
      startDestination = NavScreen.PeopleList.route
   ) {
      composable(
         route = NavScreen.PeopleList.route,

         enterTransition = {
            logDebug(tag, "Navigate to PeopleListScreen() enterTransition")
            slideIntoContainer(left, timeSpan)
         },
         exitTransition = {
            logDebug(tag, "Navigate to PeopleListScreen() exitTransition")
            slideOutOfContainer(right, timeSpan)
         },
         popEnterTransition = {
            logDebug(tag, "Navigate to PeopleListScreen() popEnterTransition")
            slideIntoContainer(down, timeSpan)
         },
         popExitTransition = {
            logDebug(tag, "Navigate to PeopleListScreen() popExitTransition")
            slideOutOfContainer(up, timeSpan)
         }
      ) {

         PeopleListScreen(
            navController = navHostController,
            viewModel = peopleViewModel
         )
      }

      composable(
         route = NavScreen.PersonInput.route,
         enterTransition = {
            logDebug(tag, "Navigate to PersonInputScreen() enterTransition")
            slideIntoContainer(right, timeSpan)
         },
         exitTransition = {
            logDebug(tag, "Navigate to PersonInputScreen() exitTransition")
            slideOutOfContainer(left, timeSpan)
         },
         popEnterTransition = {
            logDebug(tag, "Navigate to PersonInputScreen() popEnterTransition")
            slideIntoContainer(down, timeSpan)
         },
         popExitTransition = {
            logDebug(tag, "Navigate to PersonInputScreen() popExitTransition")
            slideOutOfContainer(up, timeSpan)
         }
      ) {
         PersonInputScreen(
            navController = navHostController,
            viewModel = peopleViewModel
         )
      }

      composable(
         route = NavScreen.PersonDetail.route + "/{personId}",
         arguments = listOf(navArgument("personId") { type = NavType.StringType}),
      ) { backStackEntry ->
         val id = backStackEntry.arguments?.getString("personId")?.let{
            UUID.fromString(it)
         }
         logDebug(tag, "Navigate to PersonDetailScreen() id=$id")
         PersonDetailScreen(
            id = id,
            navController = navHostController,
            viewModel = peopleViewModel
         )
      }
   }
}