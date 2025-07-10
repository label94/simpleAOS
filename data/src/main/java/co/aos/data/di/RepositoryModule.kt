package co.aos.data.di

import co.aos.data.repository.WebViewConfigRepositoryImpl
import co.aos.domain.repository.WebViewConfigRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Repository di 관리 모듈
 * */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    /** 웹뷰 설정 관련 주입 */
    @Binds
    @Singleton
    abstract fun bindWebViewConfigRepository(
        webViewConfigRepositoryImpl: WebViewConfigRepositoryImpl
    ): WebViewConfigRepository
}