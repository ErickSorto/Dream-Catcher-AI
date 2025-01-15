package org.ballistic.dreamjournalai.shared.dream_notifications.domain

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

/**
 * We'll implement this in each platform to produce a [DataStore<Preferences>]
 * with a platform-specific file location or context usage.
 */
expect fun createDataStore(): DataStore<Preferences>

/**
 * The name for our DataStore file.
 * The default "filename.preferences_pb" is common on both platforms.
 */
internal const val DATASTORE_FILE_NAME = "notification_preferences.preferences_pb"
