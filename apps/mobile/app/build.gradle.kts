plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.languify"
    compileSdk {
        version = release(36)
    }
    defaultConfig {
        applicationId = "com.languify"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    packagingOptions {
        resources {
            excludes += setOf(
                "META-INF/INDEX.LIST",
                "META-INF/DEPENDENCIES",
                "META-INF/io.netty.versions.properties"
            )
        }
    }
}

dependencies {
    //depêndencias do retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Outras dependências que já tens
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.3")
    implementation("androidx.activity:activity-compose:1.9.2")
    implementation("androidx.compose.material3:material3:1.3.0")


    // Navegação base do Jetpack Compose
    implementation("androidx.navigation:navigation-compose:2.8.3")
    implementation("androidx.compose.animation:animation:1.7.3")


    // Navigation animation for Jetpack Compose
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("androidx.compose.animation:animation:1.6.8")

    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    // Jetpack DataStore (Preferences)
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.datastore:datastore-core:1.1.1")


    // ... other dependencies
    implementation("androidx.compose.material:material-icons-extended:<version>")

    // Compose BOM (version manager)
    implementation(platform("androidx.compose:compose-bom:2024.09.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation(libs.androidx.navigation.compose)
    implementation(libs.firebase.appdistribution.gradle)
    implementation(libs.play.services.maps)
    debugImplementation("androidx.compose.ui:ui-tooling")

// Navigation
    implementation("androidx.navigation:navigation-compose:2.8.0")

// Lifecycle ViewModel
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.2")

// Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

// Retrofit (for later backend integration)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

// OkHttp logging (optional)
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

//OpenStreetMap
    implementation("org.osmdroid:osmdroid-android:6.1.18")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}