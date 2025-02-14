package pion.tech.pionbase.framework.di

import android.content.SharedPreferences
import com.piontech.data.local.PreferencesDataSource
import com.piontech.data.local.SharedPreferencesDataSource
import com.piontech.data.repository.PreferencesRepositoryImpl
import com.piontech.domain.repository.PreferencesRepository
import com.piontech.domain.usecase.PreferencesUseCase
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
    fun providePreferencesUseCase(preferencesRepository: PreferencesRepository): PreferencesUseCase {
        return PreferencesUseCase(preferencesRepository)
    }

}