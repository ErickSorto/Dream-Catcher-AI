package org.ballistic.dreamjournalai.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.ballistic.dreamjournalai.feature_dream.data.remote.OpenAITextApi
import org.ballistic.dreamjournalai.feature_dream.data.repository.OpenAIRepositoryImpl
import org.ballistic.dreamjournalai.feature_dream.domain.repository.OpenAIRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OpenAIModule {

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(240, TimeUnit.SECONDS)
        .readTimeout(240, TimeUnit.SECONDS)
        .writeTimeout(240, TimeUnit.SECONDS).addInterceptor(httpLoggingInterceptor).build()


//    @Provides
//    @Singleton
//    fun provideOpenAITextApi(
//        okHttpClient: OkHttpClient
//    ): OpenAITextApi {
//        return Retrofit.Builder()
//            .baseUrl("https://api.openai.com/v1/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(okHttpClient)
//            .build()
//            .create(OpenAITextApi::class.java)
//    }

    @Provides
    @Singleton
    fun provideOpenAIChatApi(
        okHttpClient: OkHttpClient
    ): OpenAITextApi {
        return Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/chat/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(OpenAITextApi::class.java)
    }


    @Provides
    @Singleton
    fun provideOpenAIRepository(
        api: OpenAITextApi
    ): OpenAIRepository {
        return OpenAIRepositoryImpl(api)
    }
}