package pion.tech.pionbase.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import pion.tech.pionbase.home.data.api.ApiInterface
import pion.tech.pionbase.app.data.dataStore.PreferencesDataSource
import pion.tech.pionbase.app.data.dataStore.DataStoreSource
import pion.tech.pionbase.home.data.repository.ApiRepositoryImpl
import pion.tech.pionbase.app.data.repository.DataStoreRepositoryImpl
import pion.tech.pionbase.app.data.repository.RemoteConfigRepositoryImpl
import pion.tech.pionbase.home.domain.repository.ApiRepository
import pion.tech.pionbase.app.domain.repository.DataStoreRepository
import pion.tech.pionbase.app.domain.repository.RemoteConfigRepository
import pion.tech.pionbase.home.domain.usecase.AppCategoryUseCase
import pion.tech.pionbase.app.domain.usecase.FetchRemoteConfigUseCase
import pion.tech.pionbase.language.domain.usecase.GetLanguageUseCase
import pion.tech.pionbase.app.domain.usecase.DataStoreUseCase
import pion.tech.pionbase.home.domain.usecase.TemplateUseCase
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
    fun providePreferencesUseCase(dataStoreRepository: DataStoreRepository): DataStoreUseCase {
        return DataStoreUseCase(dataStoreRepository)
    }

    @Provides
    fun provideLanguageUseCase(): GetLanguageUseCase {
        return GetLanguageUseCase()
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