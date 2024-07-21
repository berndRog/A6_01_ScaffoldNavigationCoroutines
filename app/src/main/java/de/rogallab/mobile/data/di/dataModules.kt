package de.rogallab.mobile.data.di

import org.koin.dsl.module

val dataModules = module {

//   single {
//      Room.databaseBuilder(androidContext(), AppDatabase::class.java, "app_database")
//         .build()
//   }
//   single { get<AppDatabase>().userDao() }

//   logInfo("[Koin]", "singleOf -> GenericRepository")
//   singleOf(::GenericRepository)
//   logInfo("[Koin]", "singleOf -> IOwnersRepository")
//   singleOf(::OwnersRepository) { bind<IOwnersRepository>()  }

}