package org.ballistic.dreamjournalai.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.ballistic.dreamjournalai.user_authentication.data.repository.AuthRepositoryImpl
import org.ballistic.dreamjournalai.user_authentication.domain.repository.AuthRepository


@Module
@InstallIn(SingletonComponent::class)
object SignInModule {

    @Provides
    fun provideFirebaseAuth() = Firebase.auth

    @Provides
    fun provideFirebaseFirestore() = Firebase.firestore

    @Provides
    fun provideAuthRepository(
        auth: FirebaseAuth,
        db: FirebaseFirestore,
    ): AuthRepository = AuthRepositoryImpl(
        auth = auth,
        db = db,
    )
}