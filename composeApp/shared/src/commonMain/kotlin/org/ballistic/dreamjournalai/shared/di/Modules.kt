package org.ballistic.dreamjournalai.shared.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.storage.storage
import org.ballistic.dreamjournalai.shared.core.data.DictionaryRepositoryImpl
import org.ballistic.dreamjournalai.shared.core.domain.DictionaryRepository
import org.ballistic.dreamjournalai.shared.dream_add_edit.presentation.viewmodel.AddEditDreamViewModel
import org.ballistic.dreamjournalai.shared.dream_authentication.data.repository.AuthRepositoryImpl
import org.ballistic.dreamjournalai.shared.dream_authentication.domain.repository.AuthRepository
import org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.viewmodel.LoginViewModel
import org.ballistic.dreamjournalai.shared.dream_authentication.presentation.signup_screen.viewmodel.SignupViewModel
import org.ballistic.dreamjournalai.shared.dream_favorites.presentation.viewmodel.DreamFavoriteScreenViewModel
import org.ballistic.dreamjournalai.shared.dream_fullscreen.FullScreenViewModel
import org.ballistic.dreamjournalai.shared.dream_journal_list.data.DreamRepositoryImpl
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.repository.DreamRepository
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.use_case.AddDream
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.use_case.DeleteDream
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.use_case.DreamUseCases
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.use_case.FlagDream
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.use_case.GetCurrentDreamID
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.use_case.GetDream
import org.ballistic.dreamjournalai.shared.dream_journal_list.domain.use_case.GetDreams
import org.ballistic.dreamjournalai.shared.dream_journal_list.presentation.viewmodel.DreamJournalListViewModel
import org.ballistic.dreamjournalai.shared.dream_main.presentation.viewmodel.MainScreenViewModel
import org.ballistic.dreamjournalai.shared.dream_nightmares.presentation.viewmodel.DreamNightmareScreenViewModel
import org.ballistic.dreamjournalai.shared.dream_notifications.data.repository.NotificationRepositoryImpl
import org.ballistic.dreamjournalai.shared.dream_notifications.domain.NotificationRepository
import org.ballistic.dreamjournalai.shared.dream_notifications.domain.createDataStore
import org.ballistic.dreamjournalai.shared.dream_notifications.domain.usecases.ScheduleDailyReminderUseCase
import org.ballistic.dreamjournalai.shared.dream_notifications.domain.usecases.ScheduleLucidityNotificationUseCase
import org.ballistic.dreamjournalai.shared.dream_notifications.presentation.viewmodel.NotificationScreenViewModel
import org.ballistic.dreamjournalai.shared.dream_statistics.presentation.viewmodel.DreamStatisticScreenViewModel
import org.ballistic.dreamjournalai.shared.dream_store.data.repository.RevenueCatBillingRepositoryImpl
import org.ballistic.dreamjournalai.shared.dream_store.domain.repository.BillingRepository
import org.ballistic.dreamjournalai.shared.dream_store.presentation.store_screen.viewmodel.StoreScreenViewModel
import org.ballistic.dreamjournalai.shared.dream_symbols.presentation.viewmodel.DictionaryScreenViewModel
import org.ballistic.dreamjournalai.shared.dream_tools.data.MassInterpretationRepositoryImpl
import org.ballistic.dreamjournalai.shared.dream_tools.domain.MassInterpretationRepository
import org.ballistic.dreamjournalai.shared.dream_tools.presentation.dream_tools_screen.viewmodel.DreamToolsScreenViewModel
import org.ballistic.dreamjournalai.shared.dream_tools.presentation.interpret_dreams_screen.viewmodel.InterpretDreamsViewModel
import org.ballistic.dreamjournalai.shared.dream_tools.presentation.random_dream_screen.RandomDreamToolScreenViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

expect val platformModule: Module

val appModule = module {
    single<DictionaryRepository> {
        DictionaryRepositoryImpl(fileReader = get())
    }
    single { Firebase.firestore }
    single { Firebase.auth }
    single { Firebase.storage }


    single<MassInterpretationRepository> { MassInterpretationRepositoryImpl(get()) }
    single<DreamRepository> { DreamRepositoryImpl(get()) }

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

    single<DataStore<Preferences>> {
        createDataStore()
    }
}

val billingModule = module {
    single<BillingRepository> {
        RevenueCatBillingRepositoryImpl()
    }
}

val viewModelModule = module {
    viewModelOf(::AddEditDreamViewModel)
    viewModelOf(::DreamFavoriteScreenViewModel)
    viewModelOf(::DreamJournalListViewModel)
    viewModelOf(::DreamNightmareScreenViewModel)
//    viewModelOf(::NotificationScreenViewModel)
    viewModelOf(::StoreScreenViewModel)
    viewModelOf(::DictionaryScreenViewModel)
    viewModelOf(::RandomDreamToolScreenViewModel)
    viewModelOf(::DreamToolsScreenViewModel)
    viewModelOf(::InterpretDreamsViewModel)
    viewModelOf(::MainScreenViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::SignupViewModel)
    viewModelOf(::DreamStatisticScreenViewModel)
    viewModelOf(::FullScreenViewModel)
}

//val notificationModule = module {
//    singleOf(::NotificationPreferences)
//    singleOf(::ScheduleDailyReminderUseCase)
//    singleOf(::ScheduleLucidityNotificationUseCase)
//    singleOf(::NotificationRepositoryImpl) { bind<NotificationRepository>() }
//}

val signInModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
}





