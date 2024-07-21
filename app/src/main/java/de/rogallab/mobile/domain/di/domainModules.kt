package de.rogallab.mobile.domain.di

import de.rogallab.mobile.domain.utilities.logInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.dsl.module

val domainModules: Module = module {

   logInfo("[Koin]", "single -> CoroutineDispatcher")
   single<CoroutineDispatcher> { Dispatchers.IO }

}