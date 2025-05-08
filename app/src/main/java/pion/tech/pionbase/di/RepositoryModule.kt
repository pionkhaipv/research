package pion.tech.pionbase.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pion.tech.pionbase.main.data.repository.DataStoreRepositoryImpl
import pion.tech.pionbase.main.data.repository.RemoteConfigRepositoryImpl
import pion.tech.pionbase.main.domain.repository.DataStoreRepository
import pion.tech.pionbase.main.domain.repository.RemoteConfigRepository
import pion.tech.pionbase.home.data.api.ApiInterface
import pion.tech.pionbase.home.data.repository.ApiRepositoryImpl
import pion.tech.pionbase.home.domain.repository.ApiRepository
import pion.tech.pionbase.language.data.repository.LanguageRepositoryImpl
import pion.tech.pionbase.language.domain.repository.LanguageRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

    @Provides
    @Singleton
    fun providePreferencesRepository(
        dataStore: DataStore<Preferences>
    ): DataStoreRepository {
        return DataStoreRepositoryImpl(dataStore)
    }

    @Provides
    @Singleton
    fun provideLanguageRepository(
    ): LanguageRepository {
        return LanguageRepositoryImpl()
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


}