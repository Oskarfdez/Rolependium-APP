plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.example.login"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.login"
        minSdk = 25
        targetSdk = 34
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
    packaging {
        resources {
            excludes += "META-INF/gradle/incremental.annotation.processors"
        }
    }
}

dependencies {

    // Fundamentos de Jetpack Compose
    implementation (libs.ui)
    implementation (libs.androidx.material)
    implementation(libs.androidx.compose.material.material)

    implementation (libs.androidx.activity.compose)

    //FireBase
    implementation(platform(libs.firebase.bom.v3380))
    implementation (libs.com.google.firebase.firebase.firestore)
    implementation(libs.xcom.firebase.auth)
    implementation(libs.firebase.database.v2031)
    implementation (libs.com.google.firebase.firebase.auth) // Para Firebase Authentication


    implementation (libs.hilt.android)

    implementation (libs.androidx.material.icons.extended)

    implementation(libs.hilt.android.v250)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.foundation.android)
    implementation(libs.firebase.storage.ktx)
    implementation(libs.volley)
    implementation(libs.androidx.runtime.livedata)
    kapt(libs.hilt.compiler)

    implementation(libs.material)

    implementation(libs.androidx.foundation)
    implementation(libs.hilt.compiler)
    implementation (libs.androidx.constraintlayout.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx.v261)
    implementation (libs.androidx.lifecycle.runtime.compose)
    implementation (libs.material3)
    implementation(libs.androidx.navigation.compose)
    implementation (libs.androidx.core.ktx)
    implementation(libs.androidx.navigation.compose.v260)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.core.ktx)
    implementation (libs.androidx.lifecycle.livedata)
    implementation (libs.androidx.lifecycle.runtime)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    debugImplementation (libs.ui.test.manifest)
    androidTestImplementation (libs.ui.test.junit4)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // Jetpack Compose BOM
    implementation(platform(libs.androidx.compose.bom.v20240500))

// UI core
    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.ui.tooling.preview)

// Material 3
    implementation(libs.androidx.material3.v132) // o más reciente



// Foundation (para Box, Image, etc.)
    implementation(libs.foundation)

// Image loading (usamos painterResource para cargar el PNG)
    implementation(libs.ui.graphics)

    //cargar imagenes
    implementation (libs.coil.compose)
    implementation(libs.androidx.activity.compose.v172)


}
apply(plugin = "dagger.hilt.android.plugin")