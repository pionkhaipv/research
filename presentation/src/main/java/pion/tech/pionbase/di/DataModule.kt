package pion.tech.pionbase.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.piontech.data.api.ApiInterface
import com.piontech.data.local.sharePreference.PreferencesDataSource
import com.piontech.data.local.sharePreference.DataStoreSource
import com.piontech.data.repository.ApiRepositoryImpl
import com.piontech.data.repository.DataStoreRepositoryImpl
import com.piontech.data.repository.RemoteConfigRepositoryImpl
import com.piontech.domain.repository.ApiRepository
import com.piontech.domain.repository.DataStoreRepository
import com.piontech.domain.repository.RemoteConfigRepository
import com.piontech.domain.usecase.AppCategoryUseCase
import com.piontech.domain.usecase.FetchRemoteConfigUseCase
import com.piontech.domain.usecase.LanguageUseCase
import com.piontech.domain.usecase.DataStoreUseCase
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
    @Singleton
    fun providePreferencesDataSource(dataStore: DataStore<Preferences>): PreferencesDataSource {
        return DataStoreSource(dataStore)
    }

    @Provides
    @Singleton
    fun providePreferencesRepository(
        preferencesDataSource: PreferencesDataSource
    ): DataStoreRepository {
        return DataStoreRepositoryImpl(preferencesDataSource)
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
    fun providePreferencesUseCase(dataStoreRepository: DataStoreRepository): DataStoreUseCase {
        return DataStoreUseCase(dataStoreRepository)
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