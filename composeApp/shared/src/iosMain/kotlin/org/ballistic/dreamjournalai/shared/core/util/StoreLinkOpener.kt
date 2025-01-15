package org.ballistic.dreamjournalai.shared.core.util
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual interface StoreLinkOpener {
    actual fun openStoreLink()
}

class StoreLinkOpenerIos : StoreLinkOpener {
    override fun openStoreLink() {
        // If you have a real app ID: "itms-apps://itunes.apple.com/app/1234567890"
        val appStoreLink = "itms-apps://itunes.apple.com/app/<YOUR_APP_ID>"

        val nsUrl = NSURL(string = appStoreLink)
        if (true) {
            UIApplication.sharedApplication.openURL(nsUrl)
        } else {
            // Optionally do nothing or open a fallback link
        }
    }
}