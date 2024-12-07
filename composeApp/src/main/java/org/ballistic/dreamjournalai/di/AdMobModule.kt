package org.ballistic.dreamjournalai.di

import org.ballistic.dreamjournalai.dream_ad.data.AdManagerRepositoryImpl
import org.ballistic.dreamjournalai.dream_ad.domain.AdManagerRepository
import org.koin.core.qualifier.named
import org.koin.dsl.module


val adMobModule = module {
    single(named("adUnitId")) { "ca-app-pub-8710979310678386/8178296701" }
    single<AdManagerRepository> { AdManagerRepositoryImpl(get(named("adUnitId"))) }
}