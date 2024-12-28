package de.rogallab.mobile

import android.app.Application
import de.rogallab.mobile.domain.utilities.logInfo
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

class AppStart : Application() {

   override fun onCreate() {
      super.onCreate()

      logInfo(TAG, "onCreate(): startKoin{...}")
      startKoin {
         // Log Koin into Android logger
         androidLogger(Level.DEBUG)
         // Reference Android context
         androidContext(this@AppStart)
         // Load modules
         modules(domainModules, dataModules, uiModules)
      }
   }


   companion object {
      const val IS_INFO = true
      const val IS_DEBUG = true
      const val IS_VERBOSE = true
      private const val TAG = "<-AppApplication"
   }

}
