package org.ballistic.dreamjournalai.shared.di

import android.app.Activity
import android.content.Context
import com.mikhailovskii.inappreview.InAppReviewDelegate
import com.mikhailovskii.inappreview.googlePlay.GooglePlayInAppReviewInitParams
import com.mikhailovskii.inappreview.googlePlay.GooglePlayInAppReviewManager
import org.ballistic.dreamjournalai.shared.core.data.ReviewHelperImpl
import org.ballistic.dreamjournalai.shared.core.domain.DictionaryFileReader
import org.ballistic.dreamjournalai.shared.core.domain.ReviewHelper
import org.ballistic.dreamjournalai.shared.core.domain.VibratorUtil
import org.ballistic.dreamjournalai.shared.core.domain.VibratorUtilImpl
import org.ballistic.dreamjournalai.shared.core.util.StoreLinkOpener
import org.ballistic.dreamjournalai.shared.core.util.StoreLinkOpenerAndroid
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    // If you need a Context to open assets, use 'androidApplication()'
    single {
        val context: Context = androidApplication()
        DictionaryFileReader(context)
    }

    single<VibratorUtil> {
        VibratorUtilImpl(
            context = androidApplication()
        )
    }

    single<InAppReviewDelegate> {
        val googlePlayParams = GooglePlayInAppReviewInitParams(
            activity = androidContext() as Activity
        )
        GooglePlayInAppReviewManager(googlePlayParams)
    }
    // Then a single for the ReviewHelper
    single<ReviewHelper> {
        ReviewHelperImpl(reviewDelegate = get())
    }

    single<StoreLinkOpener> {
        StoreLinkOpenerAndroid(context = androidContext())
    }
}