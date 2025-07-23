package pion.tech.pionbase.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pion.tech.pionbase.feature.home.data.api.ApiInterface
import pion.tech.pionbase.feature.home.data.repository.ApiRepositoryImpl
import pion.tech.pionbase.feature.home.data.repository.InstalledAppsRepositoryImpl
import pion.tech.pionbase.feature.home.domain.repository.ApiRepository
import pion.tech.pionbase.feature.home.domain.repository.InstalledAppsRepository
import pion.tech.pionbase.feature.language.data.repository.LanguageRepositoryImpl
import pion.tech.pionbase.feature.language.domain.repository.LanguageRepository
import pion.tech.pionbase.app.data.repository.DataStoreRepositoryImpl
import pion.tech.pionbase.app.data.repository.RemoteConfigRepositoryImpl
import pion.tech.pionbase.app.domain.repository.DataStoreRepository
import pion.tech.pionbase.app.domain.repository.RemoteConfigRepository
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

    @Provides
    @Singleton
    fun provideInstalledAppsRepository(
        @ApplicationContext context: Context
    ): InstalledAppsRepository {
        return InstalledAppsRepositoryImpl(context)
    }

}
