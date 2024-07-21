plugins {
   id("com.android.application")
   id("org.jetbrains.kotlin.android")
}

android {
   namespace = "de.rogallab.mobile"
   compileSdk = 34

   defaultConfig {
      applicationId = "de.rogallab.mobile"
      minSdk = 26
      targetSdk = 34
      versionCode = 1
      versionName = "1.0"

      testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
      vectorDrawables {
         useSupportLibrary = true
      }
   }

   buildTypes {
      release {
         isMinifyEnabled = false
         proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      }
   }
   compileOptions {
      sourceCompatibility = JavaVersion.VERSION_17
      targetCompatibility = JavaVersion.VERSION_17
   }
   kotlinOptions {
      // kotlin Version 1.9.10
      jvmTarget = "17"
   }
   kotlin {
      jvmToolchain(17)
   }
   lint {
      abortOnError = false
      disable  += "unchecked"
   }
   buildFeatures {
      compose = true
   }
   composeOptions {
      kotlinCompilerExtensionVersion = "1.5.3"
   }
   packaging {
      resources {
         excludes += "/META-INF/{AL2.0,LGPL2.1}"
         excludes += "/META-INF/LICENSE.md"
         excludes += "/META-INF/LICENSE-notice.md"
      }
   }
}

dependencies {

   // https://developer.android.com/jetpack/androidx/releases/activity
   val activityCompose = "1.9.0"
   implementation("androidx.activity:activity-compose:$activityCompose")

   // https://developer.android.com/jetpack/androidx/releases/core
   val core = "1.13.1"
   implementation("androidx.core:core-ktx:$core")

   // A BOM is a Maven module that declares a set of libraries with their versions.
   // It will greatly simplify the way you define Compose library versions in your
   // Gradle dependencies block.
   // https://developer.android.com/jetpack/compose/bom/bom-mapping
   val compose = "1.6.8"
   implementation(platform("androidx.compose:compose-bom:2024.06.00"))
   implementation("androidx.compose.ui:ui")
   implementation("androidx.compose.ui:ui-graphics")
   implementation("androidx.compose.ui:ui-tooling-preview")
   val material3 = "1.2.1"
   implementation("androidx.compose.material3:material3:$material3")
   implementation("androidx.compose.material:material-icons-extended:$compose")

   // Lifecycle,
   // https://developer.android.com/jetpack/androidx/releases/lifecycle
   val lifecycle = "2.8.3"
   // val archVersion = "2.2.0"
   // ViewModel
   implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle")
   // ViewModel utilities for Compose
   implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle")
   implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle")
   // LiveData
   // implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle")
   // Lifecycle utilities for Compose
   implementation ("androidx.lifecycle:lifecycle-runtime-compose:$lifecycle")

   // Navigation
   // https://developer.android.com/jetpack/androidx/releases/navigation
   val navigation = "2.7.7"
   implementation( "androidx.navigation:navigation-ui-ktx:$navigation")
   implementation("androidx.navigation:navigation-compose:$navigation")
   // Jetpack Compose Integration
   implementation("androidx.navigation:navigation-compose:$navigation")

   // Coroutines
   // https://kotlinlang.org/docs/releases.html
   val kotlinCoroutines = "1.8.1"
   implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutines")
   implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinCoroutines")

   // Image loading
   // https://coil-kt.github.io/coil/
   val coilCompose = "2.7.0"
   implementation("io.coil-kt:coil-compose:$coilCompose")


   // Koin
   // https://insert-koin.io/docs/3.2.0/getting-started/android/
   val koin = "3.5.6"
   implementation(platform("io.insert-koin:koin-bom:$koin"))
   implementation("io.insert-koin:koin-android:$koin")
   implementation("io.insert-koin:koin-androidx-compose:$koin")


   // TESTS -----------------------
   testImplementation("junit:junit:4.13.2")

   // Koin Test features
   testImplementation("io.insert-koin:koin-test:$koin")
   // Koin for JUnit 4
   testImplementation("io.insert-koin:koin-test-junit4:$koin")
   // Koin for JUnit 5
   testImplementation("io.insert-koin:koin-test-junit5:$koin")

   // ANDROID TESTS ---------------
   // https://developer.android.com/jetpack/androidx/releases/test
   val androidTestCore = "1.6.1"
   // To use the androidx.test.core APIs
   androidTestImplementation("androidx.test:core:$androidTestCore")
   androidTestImplementation("androidx.test:core-ktx:$androidTestCore")

   // To use the androidx.test.espresso
   val espresso = "3.6.1"
   androidTestImplementation("androidx.test.espresso:espresso-core:$espresso")

   // To use the JUnit Extension APIs
   val extJunit = "1.2.1"
   androidTestImplementation("androidx.test.ext:junit:$extJunit")
   androidTestImplementation("androidx.test.ext:junit-ktx:$extJunit")

   // To use the Truth Extension APIs
   val truth = "1.6.0"
   androidTestImplementation("androidx.test.ext:truth:$truth")

   // To use the androidx.test.runner APIs
   val runner = "1.6.1"
   androidTestImplementation("androidx.test:runner:$runner")

   // To use Compose Testing
   androidTestImplementation(platform("androidx.compose:compose-bom:2024.06.00"))
   androidTestImplementation("androidx.compose.ui:ui-test-junit4")
   debugImplementation("androidx.compose.ui:ui-tooling")
   val uiTestManifest = "1.6.8"
   debugImplementation("androidx.compose.ui:ui-test-manifest:$uiTestManifest")

   androidTestImplementation("androidx.navigation:navigation-testing:$navigation")
   androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutines")

   // Koin Test features
   androidTestImplementation("io.insert-koin:koin-test:$koin")
   // Koin for JUnit 4
   androidTestImplementation("io.insert-koin:koin-test-junit4:$koin")
   // Koin for JUnit 5
   androidTestImplementation("io.insert-koin:koin-test-junit5:$koin")


}