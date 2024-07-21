package de.rogallab.mobile

import android.app.Application
import de.rogallab.mobile.data.di.dataModules
import de.rogallab.mobile.domain.di.domainModules
import de.rogallab.mobile.ui.di.uiModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class AppStart: Application() {
    override fun onCreate() {
        super.onCreate()

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

      private const val tag = "ok>AppStart           ."
      const val isInfo = true
      const val isDebug = true
      const val isVerbose = true

      // retrofit
      // const val BASEURL = "http://192.168.178.23:5100/banking/v3"
      // const val base_url: String = "http://192.168.178.23:5010/banking/v3"

      // ktor
      // const val BASEURL = "10.0.2.2:5100/banking/v3"
      const val BASEURL = "192.168.178.23:5100/banking/v3"

      // Android Device
      // const val ANDROIDURL = "192.168.178.122"

      //

   }
}