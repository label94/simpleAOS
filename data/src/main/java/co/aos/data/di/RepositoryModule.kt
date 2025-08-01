package co.aos.data.di

import co.aos.data.repository.FileChooserRepositoryImpl
import co.aos.data.repository.NetworkStatusRepositoryImpl
import co.aos.data.repository.OcrRepositoryImpl
import co.aos.data.repository.WebViewConfigRepositoryImpl
import co.aos.domain.repository.FileChooserRepository
import co.aos.domain.repository.NetworkStatusRepository
import co.aos.domain.repository.OcrRepository
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

    /** 파일 업로드 관련 주입 */
    @Binds
    @Singleton
    abstract fun bindFileChooserRepository(
        fileChooserRepositoryImpl: FileChooserRepositoryImpl
    ): FileChooserRepository

    /** OCR 관련 주입 */
    @Binds
    @Singleton
    abstract fun bindOcrRepository(
        ocrRepositoryImpl: OcrRepositoryImpl
    ): OcrRepository

    /** 네트워크 상태 관련 주입 */
    @Binds
    @Singleton
    abstract fun bindNetworkStatusRepository(
        networkStatusRepositoryImpl: NetworkStatusRepositoryImpl
    ): NetworkStatusRepository
}