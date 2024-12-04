package org.ballistic.dreamjournalai.di

import org.ballistic.dreamjournalai.dream_add_edit.presentation.viewmodel.AddEditDreamViewModel
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.viewmodel.LoginViewModel
import org.ballistic.dreamjournalai.dream_authentication.presentation.signup_screen.viewmodel.SignupViewModel
import org.ballistic.dreamjournalai.dream_favorites.presentation.viewmodel.DreamFavoriteScreenViewModel
import org.ballistic.dreamjournalai.dream_fullscreen.FullScreenViewModel
import org.ballistic.dreamjournalai.dream_journal_list.presentation.viewmodel.DreamJournalListViewModel
import org.ballistic.dreamjournalai.dream_main.presentation.viewmodel.MainScreenViewModel
import org.ballistic.dreamjournalai.dream_nightmares.presentation.viewmodel.DreamNightmareScreenViewModel
import org.ballistic.dreamjournalai.dream_notifications.presentation.viewmodel.NotificationScreenViewModel
import org.ballistic.dreamjournalai.dream_statistics.presentation.viewmodel.DreamStatisticScreenViewModel
import org.ballistic.dreamjournalai.dream_store.presentation.store_screen.viewmodel.StoreScreenViewModel
import org.ballistic.dreamjournalai.dream_symbols.presentation.viewmodel.DictionaryScreenViewModel
import org.ballistic.dreamjournalai.dream_tools.presentation.dream_tools_screen.viewmodel.DreamToolsScreenViewModel
import org.ballistic.dreamjournalai.dream_tools.presentation.interpret_dreams_screen.viewmodel.InterpretDreamsViewModel
import org.ballistic.dreamjournalai.dream_tools.presentation.random_dream_screen.RandomDreamToolScreenViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(
        ::AddEditDreamViewModel
    )
    viewModelOf(
        ::DreamFavoriteScreenViewModel
    )
    viewModelOf(
        ::DreamJournalListViewModel
    )
    viewModelOf(
        ::DreamNightmareScreenViewModel
    )
    viewModelOf(
        ::NotificationScreenViewModel
    )
    viewModelOf(
        ::StoreScreenViewModel
    )
    viewModelOf(
        ::DictionaryScreenViewModel
    )
    viewModelOf(
        ::DreamToolsScreenViewModel
    )
    viewModelOf(
        ::InterpretDreamsViewModel
    )
    viewModelOf(
        ::RandomDreamToolScreenViewModel
    )
    viewModelOf(
        ::MainScreenViewModel
    )
    viewModelOf(
        ::LoginViewModel
    )
    viewModelOf(
        ::SignupViewModel
    )
    viewModelOf(
        ::DreamStatisticScreenViewModel
    )
    viewModelOf(
        ::FullScreenViewModel
    )
}