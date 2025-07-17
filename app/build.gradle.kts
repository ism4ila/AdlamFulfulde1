plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.bekisma.adlamfulfulde"
    compileSdk = 35

    flavorDimensions += "version"  // Correction: utilisation de += au lieu de ()

    defaultConfig {
        applicationId = "com.bekisma.adlamfulfulde"
        minSdk = 24
        targetSdk = 35
        versionCode = 42
        versionName = "3.5"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    productFlavors {
        create("free") {
            dimension = "version"  // Correction: ajout de =
            applicationId = "com.bekisma.adlamfulfulde"
            versionCode = 42
            versionName = "3.5"
            buildConfigField("boolean", "ENABLE_ADS", "true")
            buildConfigField("boolean", "IS_PRO_VERSION", "false")
        }
        create("pro") {
            dimension = "version"  // Correction: ajout de =
            applicationId = "com.bekisma.adlamfulfulde"
            versionCode = 42
            versionName = "3.5"
            buildConfigField("boolean", "ENABLE_ADS", "false")
            buildConfigField("boolean", "IS_PRO_VERSION", "true")
        }
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
    
    lint {
        abortOnError = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.audience.network.sdk)  // Correction: suppression des espaces
    implementation(libs.facebook)  // Correction: suppression des espaces
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("com.google.accompanist:accompanist-pager:0.34.0")
    implementation(libs.play.services.ads)
    implementation(libs.datastore.preferences)  // Correction: suppression des espaces
    implementation(libs.androidx.navigation.compose.v277)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.datastore.preferences.core.jvm)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.material3)
    
    // Simplified dependencies for premium features
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // Additional dependencies for premium features
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.5.4")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}