package co.aos.googlelogin.di

import android.content.Context
import androidx.credentials.CredentialManager
import co.aos.googlelogin.auth.GoogleLoginDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * google login 관련 di 모듈
 * */
@Module
@InstallIn(SingletonComponent::class)
object GoogleLoginModule {

    @Provides
    @Singleton
    fun provideCredentialManager(@ApplicationContext context: Context) = CredentialManager.create(context)

    @Provides
    @Singleton
    fun provideGoogleLoginDataSource(
        @ApplicationContext context: Context,
        credentialManager: CredentialManager
    ) = GoogleLoginDataSource(context, credentialManager)
}