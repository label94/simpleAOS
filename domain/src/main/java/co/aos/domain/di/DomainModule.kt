package co.aos.domain.di

import co.aos.domain.usecase.GetWebViewConfigUseCase
import co.aos.domain.usecase.IsAppFirstRunUseCase
import co.aos.domain.usecase.NetworkStatusUseCase
import co.aos.domain.usecase.ObserveCameraOcrUseCase
import co.aos.domain.usecase.RequestFileChooserUseCase
import co.aos.domain.usecase.SettingNotificationUseCase
import co.aos.domain.usecase.UpdateIsFirstRunUseCase
import co.aos.domain.usecase.impl.GetWebViewConfigUseCaseImpl
import co.aos.domain.usecase.impl.IsAppFirstRunUseCaseImpl
import co.aos.domain.usecase.impl.NetworkStatusUseCaseImpl
import co.aos.domain.usecase.impl.ObserveCameraOcrUseCaseImpl
import co.aos.domain.usecase.impl.RequestFileChooserUseCaseImpl
import co.aos.domain.usecase.impl.SettingNotificationUseCaseImpl
import co.aos.domain.usecase.impl.UpdateIsFirstRunUseCaseImpl
import co.aos.domain.usecase.newocr.NewOcrRecognizeTextUseCase
import co.aos.domain.usecase.newocr.impl.NewOcrRecognizeTextUseCaseImpl
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

    /** 네트워크 상태 관련 유스케이스 주입 */
    @Binds
    @Singleton
    abstract fun bindNetworkStatusUseCase(
        networkStatusUseCaseImpl: NetworkStatusUseCaseImpl
    ): NetworkStatusUseCase

    /** 설정 화면 내 알람 권한 관련 유스케이스 주입 */
    @Binds
    @Singleton
    abstract fun bindSettingNotificationUseCase(
        settingNotificationUseCaseImpl: SettingNotificationUseCaseImpl
    ): SettingNotificationUseCase

    /** 신규 OCR 관련 유스케이스 주입 */
    @Binds
    @Singleton
    abstract fun bindNewOcrRecognizeTextUseCase(
        newOcrRecognizeTextUseCaseImpl: NewOcrRecognizeTextUseCaseImpl
    ): NewOcrRecognizeTextUseCase

    /** 앱 최초 실행 확인 관련 유스케이스 주입 */
    @Binds
    @Singleton
    abstract fun bindIsAppFirstRunUseCase(
        isAppFirstRunUseCaseImpl: IsAppFirstRunUseCaseImpl
    ): IsAppFirstRunUseCase

    /** 앱 최초 실행 업데이트 관련 유스케이스 주입 */
    @Binds
    @Singleton
    abstract fun bindUpdateIsFirstRunUseCase(
        updateIsFirstRunUseCaseImpl: UpdateIsFirstRunUseCaseImpl
    ): UpdateIsFirstRunUseCase
}
