package co.aos.domain.di

import co.aos.domain.usecase.GetWebViewConfigUseCase
import co.aos.domain.usecase.ObserveCameraOcrUseCase
import co.aos.domain.usecase.RequestFileChooserUseCase
import co.aos.domain.usecase.impl.GetWebViewConfigUseCaseImpl
import co.aos.domain.usecase.impl.ObserveCameraOcrUseCaseImpl
import co.aos.domain.usecase.impl.RequestFileChooserUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Domain Di 모듈
 * */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class DomainModule {

    /** 웹뷰 설정 관련 유스케이스 주입 */
    @Binds
    @Singleton
    abstract fun bindGetWebViewConfigUseCase(
        getWebViewConfigUseCaseImpl: GetWebViewConfigUseCaseImpl
    ): GetWebViewConfigUseCase

    /** 파일 업로드 관련 유스케이스 주입 */
    @Binds
    @Singleton
    abstract fun bindRequestFileChooserUseCase(
        requestFileChooserUseCaseImpl: RequestFileChooserUseCaseImpl
    ): RequestFileChooserUseCase

    /** OCR 관련 유스케이스 주입 */
    @Binds
    @Singleton
    abstract fun bindObserveCameraOcrUseCase(
        observeCameraOcrUseCaseImpl: ObserveCameraOcrUseCaseImpl
    ): ObserveCameraOcrUseCase
}
