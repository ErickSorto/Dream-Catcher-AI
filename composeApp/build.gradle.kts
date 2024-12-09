plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    id("kotlin-parcelize")
    id("androidx.baselineprofile")
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0"
    alias(libs.plugins.kotlin.serialization)
}

android {
    compileSdk = 35

    defaultConfig {
        applicationId = "org.ballistic.dreamjournalai"
        minSdk = 27
        targetSdk = 35
        versionCode = 71
        versionName = "1.2.8"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    namespace = "org.ballistic.dreamjournalai"

    kapt {
        correctErrorTypes = true
    }
}

composeCompiler {
    enableStrongSkippingMode = true

    reportsDestination = layout.buildDirectory.dir("compose_compiler")
}

dependencies {
    // AndroidX dependencies
    implementation(libs.activityCompose)
    implementation(libs.appcompat)
    implementation(libs.material3)
    implementation(libs.material3WindowSize)
    implementation(libs.animation)
    implementation(libs.composeUi)
    implementation(libs.composeUiToolingPreview)
    implementation(libs.constraintLayoutCompose)
    implementation(libs.coreKtx)
    implementation(libs.coreSplashscreen)
    implementation(libs.datastorePreferences)
    implementation(libs.lifecycleExtensions)
    implementation(libs.lifecycleRuntimeCompose)
    implementation(libs.lifecycleRuntimeKtx)
    implementation(libs.lifecycleViewModelCompose)
    implementation(libs.lifecycleViewModelKtx)
    implementation(libs.materialIconsExtended)
    implementation(libs.navigationCompose)
    implementation(libs.roomKtx)
    implementation(libs.profileInstaller)
    implementation(libs.datetime)
    implementation(libs.workRuntimeKtx)
    implementation(libs.accompanistPermissions)
    implementation(libs.playReview)
    implementation(libs.playReviewKtx)

    // Testing and Debugging
    androidTestImplementation(libs.espressoCore)
    androidTestImplementation(libs.androidxJunit)
    androidTestImplementation(libs.uiTestJunit4)
    testImplementation(libs.mockk)
    debugImplementation(libs.uiTooling)

    // Testing
    debugImplementation(libs.uiTestManifest)
    testImplementation(libs.junit)

    // Compose Extensions and Libraries
    implementation(libs.animatedList)

    // Firebase
    implementation(libs.googleid)
    implementation(libs.firebaseFirestore)
    implementation(libs.firebaseFunctions)
    implementation(libs.firebaseStorage)
    implementation(libs.firebaseAuth)
    implementation(libs.firebaseAnalytics)

    // Google Play Services
    implementation(libs.playServicesAuth)
    implementation(libs.playServicesAds)

    // Google Auth
    implementation(libs.credentials)
    implementation(libs.credentialsPlayServicesAuth)

    // Koin
    implementation(libs.koinAndroidxCompose)
    implementation(libs.koinComposeNavigation)
    testImplementation(libs.koinTest)
    testImplementation(libs.koinTestJunit5)

    // Kotlin Coroutines
    implementation(libs.kotlinxCoroutinesAndroid)
    implementation(libs.kotlinxCoroutinesCore)
    implementation(libs.kotlinxCoroutinesPlayServices)

    // Networking
    implementation(libs.ktorClientOkhttp)

    // OpenAI
    implementation(platform(libs.openaiClientBom))
    implementation(libs.openaiClient)

    // Other Libraries
    implementation(libs.parcelerApi)
    kapt(libs.parceler)

    // Image Loading
    implementation(libs.coil)
    implementation(libs.coilCompose)
    implementation(libs.landscapistCoil)
    implementation(libs.landscapistPlaceholder)
    implementation(libs.landscapistPalette)
    implementation(libs.landscapistTransformation)

    // Billing Client
    implementation(libs.billingClient)

    // Chart Libraries
    implementation(libs.vicoCompose)
    implementation(libs.vicoComposeM3)
    implementation(libs.vicoCore)
    implementation(libs.ychartsDreamjournalai)

    implementation(libs.kotlin.serialization.json)
}
