package de.rogallab.mobile

import de.rogallab.mobile.data.IDataStore
import de.rogallab.mobile.data.local.datastore.DataStore
import de.rogallab.mobile.data.repositories.PeopleRepository
import de.rogallab.mobile.domain.IPeopleRepository
import de.rogallab.mobile.domain.utilities.logInfo
import de.rogallab.mobile.ui.INavigationHandler
import de.rogallab.mobile.ui.navigation.NavigationHandler
import de.rogallab.mobile.ui.people.PeopleViewModel
import de.rogallab.mobile.ui.people.PersonValidator
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val uiModules: Module = module {
   val tag = "<-uiModules"

   // Provide PeopleViewModel --------------------------------------------
   logInfo(tag, "single    -> PeopleInputValidator")
   //class PersonValidator(
   //   private val _context: Context
   //) {
   single<PersonValidator> { PersonValidator(androidContext()) }

   logInfo(tag, "viewModel -> PeopleViewModel")
   //class PeopleViewModel(
   //   private val _dataStore: IDataStore,
   //   private val _repository: IPeopleRepository,
   //   private val _validator:PersonValidator
   // ) : ViewModel() {
   viewModel<PeopleViewModel> { PeopleViewModel(get(), get() ) }
}


val domainModules: Module = module {
   val tag = "<-domainModules"
   logInfo(tag, "nothing do do")
}

val dataModules = module {
   val tag = "<-dataModules"

   // Provide IDataStore
   logInfo(tag, "single    -> DataStore: IDataStore")
   //class DataStore(
   //   private val _context: Context
   //): IDataStore {
   single<IDataStore> { DataStore(androidContext()) }

   // Provide IPeopleRepository
   logInfo(tag, "single    -> PeopleRepository: IPeopleRepository")
   //class PeopleRepository(
   //   private val _dataStore: IDataStore
   //): IPeopleRepository {
   single<IPeopleRepository> { PeopleRepository(get()) }
}
