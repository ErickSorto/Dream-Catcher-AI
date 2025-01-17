package org.ballistic.dreamjournalai.shared.di

import android.app.Activity
import com.mikhailovskii.inappreview.InAppReviewDelegate
import com.mikhailovskii.inappreview.googlePlay.GooglePlayInAppReviewInitParams
import com.mikhailovskii.inappreview.googlePlay.GooglePlayInAppReviewManager
import org.ballistic.dreamjournalai.shared.core.domain.DictionaryFileReader
import org.ballistic.dreamjournalai.shared.core.domain.ReviewComponent
import org.ballistic.dreamjournalai.shared.core.domain.VibratorUtil
import org.ballistic.dreamjournalai.shared.core.domain.VibratorUtilImpl
import org.ballistic.dreamjournalai.shared.core.util.StoreLinkOpener
import org.ballistic.dreamjournalai.shared.core.util.StoreLinkOpenerAndroid
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single {
        DictionaryFileReader(androidApplication())
    }

    single<VibratorUtil> {
        VibratorUtilImpl(context = androidApplication())
    }

//    factory<InAppReviewDelegate> { (act: Activity) ->
//        // Now you have a real activity from parameters
//        val googlePlayParams = GooglePlayInAppReviewInitParams(act)
//        GooglePlayInAppReviewManager(googlePlayParams)
//    }
//
//    // Add a single for ReviewComponent, same approach as iOS
//    single {
//        ReviewComponent(inAppReviewDelegate = get())
//    }

    single<StoreLinkOpener> {
        StoreLinkOpenerAndroid(context = androidContext())
    }
}