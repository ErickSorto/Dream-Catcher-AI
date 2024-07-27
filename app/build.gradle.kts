import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    id("kotlin-parcelize")
    id("androidx.baselineprofile")
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
}

android {
    compileSdk = 34

    defaultConfig {
        applicationId = "org.ballistic.dreamjournalai"
        minSdk = 27
        targetSdk = 34
        versionCode = 67
        versionName = "1.2.5"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val properties = Properties().apply {
            load(project.rootProject.file("local.properties").inputStream())
        }

        buildConfigField("String", "API_KEY", "\"${properties.getProperty("API_KEY")}\"")
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
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/license.txt"
            excludes += "/META-INF/NOTICE"
            excludes += "/META-INF/NOTICE.txt"
            excludes += "/META-INF/notice.txt"
            excludes += "/META-INF/ASL2.0"
            excludes += "/META-INF/*.kotlin_module"
            excludes += "/google/protobuf/*.proto"
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

    val composeVersion = "1.7.0-beta05"
    // AndroidX dependencies
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.compose.material3:material3:1.3.0-beta04")
    implementation("androidx.compose.material3:material3-window-size-class:1.2.1")
    implementation("androidx.compose.animation:animation:$composeVersion")
    implementation("androidx.compose.ui:ui:$composeVersion")
    implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.profileinstaller:profileinstaller:1.3.1")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("com.google.accompanist:accompanist-permissions:0.35.1-alpha")
    implementation("com.google.android.play:review:2.0.1")
    implementation("com.google.android.play:review-ktx:2.0.1")

    // Testing and Debugging
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
    baselineProfile(project(":baselineprofile"))
    debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")

    //noinspection GradleDependency
    debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")
    testImplementation("junit:junit:4.13.2")

    // Compose Extensions and Libraries
    implementation("com.github.SmartToolFactory:Compose-AnimatedList:0.5.1")
    implementation("com.maxkeppeler.sheets-compose-dialogs:core:1.1.0")
    implementation("com.maxkeppeler.sheets-compose-dialogs:calendar:1.1.0") // CALENDAR
    implementation("com.maxkeppeler.sheets-compose-dialogs:clock:1.1.0") // CLOCK
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-functions-ktx:21.0.0")
    implementation("com.google.firebase:firebase-storage")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // Google Play Services
    implementation("com.google.android.gms:play-services-auth:21.2.0")
    implementation("com.google.android.gms:play-services-ads:23.2.0")

    // Google Auth
    implementation("androidx.credentials:credentials:1.3.0-rc01")
    implementation("androidx.credentials:credentials-play-services-auth:1.3.0-rc01")

    // Hilt (Dependency Injection)
    implementation("com.google.dagger:hilt-android:2.49")
    kapt("com.google.dagger:hilt-android-compiler:2.49")
    kapt("androidx.hilt:hilt-compiler:1.2.0")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0-RC")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.0")

    // Networking
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("io.ktor:ktor-client-okhttp:2.3.11")

    // OpenAI
    implementation(platform("com.aallam.openai:openai-client-bom:3.8.1"))
    implementation("com.aallam.openai:openai-client")

    // Other Libraries
    implementation("com.github.skydoves:cloudy:0.1.2")
    implementation("org.parceler:parceler-api:1.1.13")
    kapt("org.parceler:parceler:1.1.13")

    // Image Loading
    implementation("io.coil-kt:coil:2.6.0")
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Billing Client
    implementation("com.android.billingclient:billing:7.0.0")

    // Chart Libraries
    implementation("com.patrykandpatrick.vico:compose:2.0.0-alpha.19")
    implementation("com.patrykandpatrick.vico:compose-m3:2.0.0-alpha.19")
    implementation("com.patrykandpatrick.vico:core:2.0.0-alpha.19")
    implementation("co.yml:ycharts-dreamjournalai:2.1.0")
}
