package org.ballistic.dreamjournalai.shared.core.util

import dev.gitlive.firebase.storage.Data

/**
 * On Android, GitLive's Data is an actual class wrapping a `ByteArray`.
 */
actual fun ByteArray.toGitLiveData(): Data {
    // The library's "Data(public val data: ByteArray)" actual constructor
    return Data(this)
}