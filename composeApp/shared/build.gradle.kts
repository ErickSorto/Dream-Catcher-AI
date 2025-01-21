import org.gradle.kotlin.dsl.implementation
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            export("io.github.mirzemehdi:kmpnotifier:1.4.0")
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha10")
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlin.stdlib)
            implementation(libs.datetime)



            //Image
            implementation(libs.landscapist.coil3)

            implementation(libs.kotlinx.serialization.json.v180rc)

            //firebase functions
            implementation(libs.googleid)
            implementation(libs.firebaseFirestore)
            implementation(libs.firebaseFunctions)
            implementation(libs.firebaseStorage)
            implementation(libs.firebaseAuth)
            implementation(libs.firebaseAnalytics)

            implementation(libs.lexilabs.basic.ads)
            implementation(libs.playServicesAds)



            // koin
            api(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.kmpauth.google) //Google One Tap Sign-In
            implementation(libs.kmpauth.firebase) //Integrated Authentications with Firebase
            implementation(libs.kmpauth.uihelper) //UiHelper SignIn buttons (AppleSignIn, GoogleSignInButton)

            //openai
            implementation(project.dependencies.platform(libs.openaiClientBom))
            implementation(libs.openaiClient)

            implementation(compose.materialIconsExtended)

            implementation("io.coil-kt.coil3:coil-compose:3.0.4")

            implementation(libs.in1.app.review.kmp.google.play)

            implementation(libs.permissions)
            implementation(libs.permissions.compose)

            implementation(libs.purchases.core)
            implementation(libs.purchases.datetime)   // Optional
            implementation(libs.purchases.either)     // Optional
            implementation(libs.purchases.result)

            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.datastore)
            implementation(libs.datastorePreferences)
            implementation(libs.koalaplot.core)
            implementation(libs.ktor.client.core)

            implementation("co.touchlab:kermit:2.0.4")
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            //koin
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.compose)

            implementation(libs.coreSplashscreen)
            implementation(libs.kmpnotifier)
            implementation(libs.vicoCore)
            implementation(libs.vicoCompose)
            implementation(libs.vicoComposeM3)
            implementation(libs.ychartsDreamjournalai)


            implementation(libs.ktor.client.okhttp)
            implementation(libs.ktor.client.android)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation("io.coil-kt.coil3:coil-network-ktor2:3.0.0")
            implementation("io.coil-kt.coil3:coil-network-ktor3:3.0.0")
        }

        iosMain {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:3.0.0")
                // Add iOS-specific dependencies here. This a source set created by Kotlin Gradle
                // Plugin (KGP) that each specific iOS target (e.g., iosX64) depends on as
                // part of KMP’s default source set hierarchy. Note that this source set depends
                // on common by default and will correctly pull the iOS artifacts of any
                // KMP dependencies declared in commonMain.
            }
        }
    }
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}
android {
    namespace = "org.ballistic.dreamjournalai.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/commonMain/resources", "src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "org.ballistic.dreamjournalai"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 71
        versionName = "1.2.8"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Adjust the signing config as necessary
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
    buildFeatures.compose = true
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.toString()
    }
}


