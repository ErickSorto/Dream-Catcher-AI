package org.ballistic.dreamjournalai.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.ballistic.dreamjournalai.core.NetworkHelper
import org.ballistic.dreamjournalai.dream_tools.data.MassInterpretationRepositoryImpl
import org.ballistic.dreamjournalai.dream_tools.domain.MassInterpretationRepository
import org.ballistic.dreamjournalai.feature_dream.data.repository.DreamRepositoryImpl
import org.ballistic.dreamjournalai.feature_dream.domain.repository.DreamRepository
import org.ballistic.dreamjournalai.feature_dream.domain.use_case.AddDream
import org.ballistic.dreamjournalai.feature_dream.domain.use_case.DeleteDream
import org.ballistic.dreamjournalai.feature_dream.domain.use_case.DreamUseCases
import org.ballistic.dreamjournalai.feature_dream.domain.use_case.GetDream
import org.ballistic.dreamjournalai.feature_dream.domain.use_case.GetDreams
import org.ballistic.dreamjournalai.onboarding.presentation.data.DataStoreRepository
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMassInterpretationRepository(
        db: FirebaseFirestore
    ): MassInterpretationRepository {
        return MassInterpretationRepositoryImpl(
            db = db
        )
    }

    @Provides
    @Singleton
    fun provideDreamRepository(
        storage: FirebaseStorage,
        db: FirebaseFirestore
    ): DreamRepository {
        return DreamRepositoryImpl(
            storage = storage,
            db = db
        )
    }

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    @Provides
    @Singleton
    fun provideDreamUseCases(
        repository: DreamRepository
    ): DreamUseCases {
        return DreamUseCases(
            getDreams = GetDreams(repository),
            deleteDream = DeleteDream(repository),
            addDream = AddDream(repository),
            getDream = GetDream(repository)
        )
    }

    @Provides
    @Singleton
    fun provideDataStoreRepository(
        @ApplicationContext context: Context
    ) = DataStoreRepository(context = context)


    @Provides
    @Singleton
    fun provideNetworkHelper(@ApplicationContext context: Context): NetworkHelper {
        return NetworkHelper(context)
    }

    @Provides
    @Singleton
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }
}