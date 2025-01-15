package org.ballistic.dreamjournalai.shared.core.util

import android.content.Context
import android.content.Intent
import android.net.Uri

actual interface StoreLinkOpener {
    actual fun openStoreLink()
}

class StoreLinkOpenerAndroid(
    private val context: Context
) : StoreLinkOpener {
    override fun openStoreLink() {
        // Example Play Store link
        val playStoreLink = "https://play.google.com/store/apps/details?id=org.ballistic.dreamjournalai"

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(playStoreLink)
            setPackage("com.android.vending") // force the Play Store app if installed
        }

        // If the intent can be resolved (Play Store installed), use it
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // Otherwise, fall back to any browser
            val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse(playStoreLink))
            context.startActivity(fallbackIntent)
        }
    }
}