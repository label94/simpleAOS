package co.aos.local.pref.di

import android.content.Context
import android.content.SharedPreferences
import co.aos.local.pref.SharedPreferenceManager
import co.aos.local.pref.consts.SharedConstants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * SharedPreference di를 위한 모듈
 * */
@Module
@InstallIn(SingletonComponent::class)
object SharedPreferenceModule {

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(SharedConstants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideSharedPreferencesEditor(sharedPreferences: SharedPreferences): SharedPreferences.Editor {
        return sharedPreferences.edit()
    }

    @Provides
    @Singleton
    fun provideSharedPreferenceManager(@ApplicationContext context: Context, sharedPreferences: SharedPreferences): SharedPreferenceManager {
        return SharedPreferenceManager(context, sharedPreferences)
    }
}