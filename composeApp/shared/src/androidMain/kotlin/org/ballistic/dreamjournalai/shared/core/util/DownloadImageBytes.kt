package org.ballistic.dreamjournalai.shared.core.util

import java.net.URL

actual suspend fun downloadImageBytes(urlString: String): ByteArray {
    return URL(urlString).readBytes()
}