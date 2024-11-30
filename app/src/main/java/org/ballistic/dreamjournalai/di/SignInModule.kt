package org.ballistic.dreamjournalai.di

import org.ballistic.dreamjournalai.dream_authentication.data.repository.AuthRepositoryImpl
import org.ballistic.dreamjournalai.dream_authentication.domain.repository.AuthRepository
import org.koin.dsl.module

val signInModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
}