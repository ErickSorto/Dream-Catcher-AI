package org.ballistic.dreamjournalai.shared.dream_notifications.domain

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath


@SuppressLint("StaticFieldLeak")
internal lateinit var androidContext: Context

actual fun createDataStore(): DataStore<Preferences> {
    // We'll assume we set "androidContext" somewhere in the Android app (like in Application class)
    val producePath = androidContext.filesDir.resolve(DATASTORE_FILE_NAME).absolutePath
    return PreferenceDataStoreFactory.createWithPath {
        producePath.toPath()
    }
}