package org.ballistic.dreamjournalai.shared.dream_notifications.domain

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.cinterop.ExperimentalForeignApi
import okio.Path.Companion.toPath
import platform.Foundation.*



@OptIn(ExperimentalForeignApi::class)
actual fun createDataStore(): DataStore<Preferences> {
    // 1) Find iOS Documents directory
    val docUrl: NSURL? = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null
    )
    requireNotNull(docUrl) { "Could not find iOS documents directory" }

    // 2) Append the file name
    val producePath = docUrl.path + "/$DATASTORE_FILE_NAME"

    // 3) Create the DataStore
    return PreferenceDataStoreFactory.createWithPath {
        producePath.toPath() // turn the string path into a kotlin Path
    }
}