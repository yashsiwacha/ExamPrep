plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.examprep.data"
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        // Room: export schema for migration history
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
            arg("room.expandProjection", "true")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    kotlinOptions {
        jvmTarget = "21"
    }
}

dependencies {
    implementation(project(":domain"))

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.bundles.coroutines)

    // Room
    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)

    // DI
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Serialization (for JSON content import)
    implementation(libs.kotlinx.serialization.json)

    // Logging
    implementation(libs.timber)

    // Testing
    testImplementation(libs.bundles.testing.unit)
    testImplementation(libs.androidx.room.testing)
    androidTestImplementation(libs.androidx.room.testing)
}
