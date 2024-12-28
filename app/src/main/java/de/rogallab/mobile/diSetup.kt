package de.rogallab.mobile

import de.rogallab.mobile.data.IDataStore
import de.rogallab.mobile.data.local.datastore.DataStore
import de.rogallab.mobile.data.repositories.PersonsRepository
import de.rogallab.mobile.domain.IPersonRepository
import de.rogallab.mobile.domain.utilities.logError
import de.rogallab.mobile.domain.utilities.logInfo
import de.rogallab.mobile.ui.IErrorHandler
import de.rogallab.mobile.ui.INavigationHandler
import de.rogallab.mobile.ui.errors.ErrorHandler
import de.rogallab.mobile.ui.navigation.NavigationHandler
import de.rogallab.mobile.ui.people.PersonViewModel
import de.rogallab.mobile.ui.people.PersonValidator
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

typealias CoroutineDispatcherMain = CoroutineDispatcher
typealias CoroutineDispatcherIo = CoroutineDispatcher
typealias CoroutineScopeMain = CoroutineScope
typealias CoroutineScopeIo = CoroutineScope

val domainModules: Module = module {
   val tag = "<-domainModules"


   logInfo(tag, "factory   -> CoroutineExceptionHandler")
   factory<CoroutineExceptionHandler> {
      CoroutineExceptionHandler { _, exception ->
         logError(tag, "Coroutine exception: ${exception.localizedMessage}")
      }
   }
   logInfo( tag, "factory  -> CoroutineDispatcherMain")
   factory<CoroutineDispatcherMain> { Dispatchers.Main }

   logInfo(tag, "factory   -> CoroutineDispatcherIo)")
   factory<CoroutineDispatcherIo>{ Dispatchers.IO }


   logInfo(tag, "factory   -> CoroutineScopeMain")
   factory<CoroutineScopeMain> {
      CoroutineScope(
         SupervisorJob() +
            get<CoroutineDispatcherIo>()
      )
   }

   logInfo(tag, "factory   -> CoroutineScopeIo")
   factory<CoroutineScopeIo> {
      CoroutineScope(
         SupervisorJob() +
            get<CoroutineDispatcherIo>()
      )
   }
}

val dataModules = module {
   val tag = "<-dataModules"

   logInfo(tag, "single    -> DataStore: IDataStore")
   single<IDataStore> {
      DataStore(
         _context = androidContext()
      )
   }

   logInfo(tag, "single    -> PersonsRepository: IPersonRepository")
   single<IPersonRepository> {
      PersonsRepository(
         _dataStore = get<IDataStore>(),
         _coroutineDispatcher = get<CoroutineDispatcherIo>(),
      )
   }
}

val uiModules: Module = module {
   val tag = "<-uiModules"

   logInfo(tag, "factory   -> NavigationoroutineExceptionHandler")
   factory<INavigationHandler> {
      NavigationHandler(
         _coroutineScopeMain = get<CoroutineScopeMain>(),
         _exceptionHandler = get<CoroutineExceptionHandler>()
      )
   }

   factory<IErrorHandler> {
      ErrorHandler(
         _coroutineScopeMain = get<CoroutineScopeMain>(),
         _exceptionHandler = get<CoroutineExceptionHandler>()
      )
   }

   logInfo(tag, "single    -> PeopleInputValidator")
   single<PersonValidator> { PersonValidator(androidContext()) }

   logInfo(tag, "viewModel -> PersonViewModel")
   viewModel<PersonViewModel> {
      PersonViewModel(
         _repository = get<IPersonRepository>(),
         _validator = get<PersonValidator>(),
         _navigationHandler = get<INavigationHandler>(),
         _errorHandler = get<IErrorHandler>(),
         _exceptionHandler = get<CoroutineExceptionHandler>()
      )
   }
}
