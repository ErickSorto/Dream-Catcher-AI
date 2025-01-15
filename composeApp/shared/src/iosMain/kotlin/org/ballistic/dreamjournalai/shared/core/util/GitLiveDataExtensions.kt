package org.ballistic.dreamjournalai.shared.core.util

import dev.gitlive.firebase.storage.Data
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData
import platform.Foundation.create
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned


/**
 * On iOS, GitLive's Data is an actual class wrapping `NSData`.
 * We must convert the `ByteArray` into an `NSData`.
 */
@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual fun ByteArray.toGitLiveData(): Data {
    // Convert ByteArray -> NSData via pinning
    val nsData = usePinned { pinned ->
        NSData.create(
            bytes = pinned.addressOf(0),    // pointer to the pinned memory
            length = this.size.toLong().toULong()
        )
    }
    // Now wrap in GitLive’s Data(actual constructor)
    return Data(nsData)
}