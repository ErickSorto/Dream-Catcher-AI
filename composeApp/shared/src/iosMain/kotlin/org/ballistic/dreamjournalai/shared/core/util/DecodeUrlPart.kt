package org.ballistic.dreamjournalai.shared.core.util

actual fun decodeUrlPart(encoded: String): String {
    // One simple approach is a minimal percent-decoding:
    // (You could also use CFURLCreateStringByReplacingPercentEscapesUsingEncoding, etc.)

    val sb = StringBuilder()
    var i = 0
    while (i < encoded.length) {
        val c = encoded[i]
        if (c == '%' && i + 2 < encoded.length) {
            val hex = encoded.substring(i + 1, i + 3)
            val code = hex.toIntOrNull(16)
            if (code != null) {
                sb.append(code.toChar())
                i += 3
                continue
            }
        }
        sb.append(c)
        i++
    }
    return sb.toString()
}