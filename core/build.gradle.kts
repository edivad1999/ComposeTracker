plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("plugin.serialization")
}

android {
    namespace = "com.edivad_99.core"
    kotlinOptions {
        freeCompilerArgs += listOf(
            "-Xcontext-receivers",
            "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
        )
    }
}

dependencies {
    api(libs.bundles.ktor)
    api(kotlinx.coroutines.core)
    api(kotlinx.serialization.json)
    api(platform(libs.firebaseBom ))
    api(libs.firestore)

}
