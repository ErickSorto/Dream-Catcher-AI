package org.ballistic.dreamjournalai.shared.core.util

// androidMain
import java.net.URLDecoder

actual fun decodeUrlPart(encoded: String): String {
    return URLDecoder.decode(encoded, "UTF-8")
}
