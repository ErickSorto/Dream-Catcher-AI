package org.ballistic.dreamjournalai.shared.core.util

expect interface StoreLinkOpener {
    /**
     * Opens the store link for the current platform.
     *  - On Android: "https://play.google.com/store/apps/details?id=..."
     *  - On iOS: "itms-apps://itunes.apple.com/app/..."
     */
    fun openStoreLink()
}