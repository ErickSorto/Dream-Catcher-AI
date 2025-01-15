buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.8.0-rc01")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
        classpath("com.google.gms:google-services:4.4.2")
    }
}

plugins {
    id("com.android.library") version "8.2.2" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false
    id("com.android.test") version "8.2.2" apply false
    id("androidx.baselineprofile") version "1.3.3" apply false

    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.googleServices) apply false
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}
