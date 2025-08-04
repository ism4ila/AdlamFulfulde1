plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.bekisma.adlamfulfulde"
    compileSdk = 34

    flavorDimensions += "version"  // Correction: utilisation de += au lieu de ()

    defaultConfig {
        applicationId = "com.bekisma.adlamfulfulde"
        minSdk = 24
        targetSdk = 34
        versionCode = 44
        versionName = "3.6.1"

        testInstrumentationRunner = "com.bekisma.adlamfulfulde.CustomTestRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        
        // Disable Firebase Analytics for AdMob only
        manifestPlaceholders["firebase_analytics_collection_enabled"] = "false"
    }

    productFlavors {
        create("free") {
            dimension = "version"
            applicationId = "com.bekisma.adlamfulfulde"
            versionCode = 45
            versionName = "3.6.1"
            buildConfigField("boolean", "ENABLE_ADS", "true")
            buildConfigField("boolean", "IS_PRO_VERSION", "false")
        }
        
        create("pro") {
            dimension = "version"
            applicationId = "com.bekisma.adlamfulfulde.pro"
            versionCode = 45
            versionName = "3.6.1"
            buildConfigField("boolean", "ENABLE_ADS", "false")
            buildConfigField("boolean", "IS_PRO_VERSION", "true")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
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
        kotlinCompilerExtensionVersion = "1.5.10"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE.txt"
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("com.google.accompanist:accompanist-pager:0.34.0")
    implementation(libs.datastore.preferences)  // Correction: suppression des espaces
    implementation(libs.androidx.navigation.compose.v277)
    implementation(libs.androidx.datastore.core.android)
    implementation(libs.androidx.datastore.preferences.core.jvm)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.material3)
    implementation("com.google.android.gms:play-services-ads:23.5.0")
    implementation("com.google.android.ump:user-messaging-platform:2.2.0")


    // Simplified dependencies for premium features
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // Additional dependencies for premium features
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.lifecycle:lifecycle-process:2.7.0")
    implementation("androidx.compose.runtime:runtime-livedata:1.5.4")
    
    // Hilt Dependency Injection
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    
    // Remove deprecated hilt-lifecycle-viewmodel dependency
    // ViewModels are now supported natively with @HiltViewModel
    

    // Unit testing
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.5.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("com.google.truth:truth:1.1.4")
    testImplementation("io.mockk:mockk:1.13.7")
    testImplementation("org.robolectric:robolectric:4.12.1")

    // Android instrumentation testing
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation("androidx.test.ext:junit-ktx:1.1.5")
    androidTestImplementation("androidx.test:core-ktx:1.5.0")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.navigation:navigation-testing:2.7.5")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
    androidTestImplementation("androidx.compose.ui:ui-test-manifest:1.5.4")
    androidTestImplementation("com.google.truth:truth:1.1.4")
    androidTestImplementation("io.mockk:mockk-android:1.13.7")
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}