package org.ballistic.dreamjournalai.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.ballistic.dreamjournalai.core.NetworkHelper
import org.ballistic.dreamjournalai.dream_journal_list.data.DreamRepositoryImpl
import org.ballistic.dreamjournalai.dream_journal_list.domain.repository.DreamRepository
import org.ballistic.dreamjournalai.dream_journal_list.domain.use_case.AddDream
import org.ballistic.dreamjournalai.dream_journal_list.domain.use_case.DeleteDream
import org.ballistic.dreamjournalai.dream_journal_list.domain.use_case.DreamUseCases
import org.ballistic.dreamjournalai.dream_journal_list.domain.use_case.FlagDream
import org.ballistic.dreamjournalai.dream_journal_list.domain.use_case.GetCurrentDreamID
import org.ballistic.dreamjournalai.dream_journal_list.domain.use_case.GetDream
import org.ballistic.dreamjournalai.dream_journal_list.domain.use_case.GetDreams
import org.ballistic.dreamjournalai.dream_onboarding.data.DataStoreRepository
import org.ballistic.dreamjournalai.dream_tools.data.MassInterpretationRepositoryImpl
import org.ballistic.dreamjournalai.dream_tools.domain.MassInterpretationRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val appModule = module {
    single<FirebaseFirestore> {  FirebaseFirestore.getInstance() }
    single<FirebaseAuth> { FirebaseAuth.getInstance() }
    single { FirebaseStorage.getInstance() }

    singleOf(::MassInterpretationRepositoryImpl) { bind<MassInterpretationRepository>() }
    singleOf(::DreamRepositoryImpl) {bind<DreamRepository>()}

    single {
        DreamUseCases(
            getDreams = GetDreams(get()),
            deleteDream = DeleteDream(get()),
            addDream = AddDream(get()),
            getDream = GetDream(get()),
            getCurrentDreamId = GetCurrentDreamID(get()),
            flagDream = FlagDream(get())
        )
    }

    single { DataStoreRepository(get())}

    single { NetworkHelper(get()) }
}