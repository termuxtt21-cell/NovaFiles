plugins {
    id("com.android.application")
}

android {
    namespace = "com.novafiles"

    compileSdk = 37

    defaultConfig {
        applicationId = "com.novafiles"

        minSdk = 26
        targetSdk = 37

        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }

        release {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    // AndroidX
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.activity:activity-compose:1.13.0")

    // Jetpack Compose
    implementation(
        platform(
            "androidx.compose:compose-bom:2026.09.00"
        )
    )

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")

    implementation(
        "androidx.compose.material3:material3"
    )

    // Material Icons
    // Material Icons
implementation(
    "androidx.compose.material:material-icons-extended"
)

    debugImplementation(
        "androidx.compose.ui:ui-tooling"
    )

    // Lifecycle
    implementation(
        "androidx.lifecycle:lifecycle-runtime-compose:2.9.4"
    )

    // Shizuku
    implementation(
        "dev.rikka.shizuku:api:13.1.5"
    )

    implementation(
        "dev.rikka.shizuku:provider:13.1.5"
    )
}
