package pion.tech.pionbase.di

import android.content.SharedPreferences
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.piontech.data.api.ApiInterface
import com.piontech.data.local.sharePreference.PreferencesDataSource
import com.piontech.data.local.sharePreference.SharedPreferencesDataSource
import com.piontech.data.repository.ApiRepositoryImpl
import com.piontech.data.repository.PreferencesRepositoryImpl
import com.piontech.data.repository.RemoteConfigRepositoryImpl
import com.piontech.domain.repository.ApiRepository
import com.piontech.domain.repository.PreferencesRepository
import com.piontech.domain.repository.RemoteConfigRepository
import com.piontech.domain.usecase.AppCategoryUseCase
import com.piontech.domain.usecase.FetchRemoteConfigUseCase
import com.piontech.domain.usecase.LanguageUseCase
import com.piontech.domain.usecase.PreferencesUseCase
import com.piontech.domain.usecase.TemplateUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataModule {

    @Provides
    fun providePreferencesDataSource(
        sharedPreferences: SharedPreferences
    ): PreferencesDataSource {
        return SharedPreferencesDataSource(sharedPreferences)
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(
        preferencesDataSource: PreferencesDataSource
    ): PreferencesRepository {
        return PreferencesRepositoryImpl(preferencesDataSource)
    }

    @Provides
    @Singleton
    fun provideRemoteConfigRepository(
        remoteConfig: FirebaseRemoteConfig
    ): RemoteConfigRepository {
        return RemoteConfigRepositoryImpl(remoteConfig)
    }

    @Provides
    @Singleton
    fun provideApiRepository(
        apiInterface: ApiInterface
    ): ApiRepository {
        return ApiRepositoryImpl(apiInterface)
    }


    @Provides
    fun providePreferencesUseCase(preferencesRepository: PreferencesRepository): PreferencesUseCase {
        return PreferencesUseCase(preferencesRepository)
    }

    @Provides
    fun provideLanguageUseCase(): LanguageUseCase {
        return LanguageUseCase()
    }

    @Provides
    fun provideFetchRemoteConfigUseCase(
        remoteConfigRepository: RemoteConfigRepository
    ): FetchRemoteConfigUseCase {
        return FetchRemoteConfigUseCase(remoteConfigRepository)
    }

    @Provides
    fun provideAppCategoryUseCase(
        apiRepository: ApiRepository
    ): AppCategoryUseCase {
        return AppCategoryUseCase(apiRepository)
    }

    @Provides
    fun provideTemplateUseCase(
        apiRepository: ApiRepository
    ): TemplateUseCase {
        return TemplateUseCase(apiRepository)
    }

}