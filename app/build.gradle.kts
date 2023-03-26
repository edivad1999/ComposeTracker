plugins {
    id("com.android.application")
    id("com.mikepenz.aboutlibraries.plugin")
    kotlin("android")
    kotlin("plugin.serialization")
    id ("com.google.gms.google-services")
    id ("com.google.firebase.crashlytics")

}
if (gradle.startParameter.taskRequests.toString().contains("Standard")) {
    apply<com.google.gms.googleservices.GoogleServicesPlugin>()
}

val SUPPORTED_ABIS = setOf("armeabi-v7a", "arm64-v8a", "x86", "x86_64")

android {
    namespace ="com.edivad_99.compose_tracker"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.edivad_99.compose_tracker"
        minSdk =28
        targetSdk =33
        versionCode =1
        versionName ="1.0"

        testInstrumentationRunner ="androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary= true
        }
        ndk {
            abiFilters += SUPPORTED_ABIS
        }
    }
    splits {
        abi {
            isEnable = true
            reset()
            include(*SUPPORTED_ABIS.toTypedArray())
            isUniversalApk = true
        }
    }

    buildTypes {
        release {
            isDebuggable = false
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
        }
        debug{
//            applicationIdSuffix = ".debug"
            isPseudoLocalesEnabled = true
        }
    }
    packagingOptions {
        resources.excludes.addAll(listOf(
            "META-INF/DEPENDENCIES",
            "LICENSE.txt",
            "META-INF/LICENSE",
            "META-INF/LICENSE.txt",
            "META-INF/README.md",
            "META-INF/NOTICE",
            "META-INF/*.kotlin_module",
        ))
    }
    dependenciesInfo {
        includeInApk = false
    }

    buildFeatures {
        viewBinding = true
        compose = true

        // Disable some unused things
        aidl = false
        renderScript = false
        shaders = false
    }

    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }

    composeOptions {
        kotlinCompilerExtensionVersion = compose.versions.compiler.get()
    }
}

dependencies {

    implementation(project(":core"))

    // Compose
    implementation(platform(compose.bom))
    implementation(compose.activity)
    implementation(compose.foundation)
    implementation(compose.material3.core)
    implementation(compose.material.core)
    implementation(compose.material.icons)
    implementation(compose.animation)
    implementation(compose.animation.graphics)
    implementation(compose.ui.tooling)
    implementation(compose.ui.util)
    implementation(compose.accompanist.webview)
    implementation(compose.accompanist.permissions)
    implementation(compose.accompanist.themeadapter)
    implementation(compose.accompanist.systemuicontroller)

    implementation(androidx.annotation)
    implementation(androidx.paging.runtime)
    implementation(androidx.paging.compose)


    implementation(kotlinx.reflect)

    implementation(platform(kotlinx.coroutines.bom))
    implementation(kotlinx.bundles.coroutines)

    // AndroidX libraries

    implementation(androidx.corektx)

    implementation(androidx.bundles.lifecycle)

    // Network client
    implementation(libs.bundles.ktor)


    // Data serialization (JSON, protobuf)
    implementation(kotlinx.bundles.serialization)

    // UI libraries
    implementation(libs.material)

    implementation(libs.aboutLibraries.compose)
    implementation(libs.bundles.voyager)

    implementation(libs.bundles.koin)



    // For detecting memory leaks; see https://square.github.io/leakcanary/
    // debugImplementation(libs.leakcanary.android)
    implementation(libs.leakcanary.plumber)

    api(platform(libs.firebaseBom ))
    api(libs.bundles.firebase)

    implementation(libs.landscapist.coil)
}