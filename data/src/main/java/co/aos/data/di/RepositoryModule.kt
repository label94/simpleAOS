package co.aos.data.di

import co.aos.data.repository.BarcodeScannerRepositoryImpl
import co.aos.data.repository.FileChooserRepositoryImpl
import co.aos.data.repository.GuideRepositoryImpl
import co.aos.data.repository.NetworkStatusRepositoryImpl
import co.aos.data.repository.NewOcrRepositoryImpl
import co.aos.data.repository.OcrRepositoryImpl
import co.aos.data.repository.SettingNotificationRepositoryImpl
import co.aos.data.repository.SplashRepositoryImpl
import co.aos.data.repository.LegacyUserRepositoryImpl
import co.aos.data.repository.UserRepositoryImpl
import co.aos.data.repository.WebViewConfigRepositoryImpl
import co.aos.data.repository.WishRepositoryImpl
import co.aos.domain.repository.BarcodeScannerRepository
import co.aos.domain.repository.FileChooserRepository
import co.aos.domain.repository.GuideRepository
import co.aos.domain.repository.NetworkStatusRepository
import co.aos.domain.repository.NewOcrRepository
import co.aos.domain.repository.OcrRepository
import co.aos.domain.repository.SettingNotificationRepository
import co.aos.domain.repository.SplashRepository
import co.aos.domain.repository.LegacyUserRepository
import co.aos.domain.repository.UserRepository
import co.aos.domain.repository.WebViewConfigRepository
import co.aos.domain.repository.WishRepository
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

    /** 스플래시 관련 Repository 주입 */
    @Binds
    @Singleton
    abstract fun bindSplashRepository(
        splashRepositoryImpl: SplashRepositoryImpl
    ): SplashRepository

    /** 최초 실행 관련 Repository 주입 */
    @Binds
    @Singleton
    abstract fun bindGuideRepository(
        guideRepositoryImpl: GuideRepositoryImpl
    ): GuideRepository

    /** 웹뷰 설정 관련 Repository 주입 */
    @Binds
    @Singleton
    abstract fun bindWebViewConfigRepository(
        webViewConfigRepositoryImpl: WebViewConfigRepositoryImpl
    ): WebViewConfigRepository

    /** 파일 업로드 관련 Repository 주입 */
    @Binds
    @Singleton
    abstract fun bindFileChooserRepository(
        fileChooserRepositoryImpl: FileChooserRepositoryImpl
    ): FileChooserRepository

    /** OCR 관련 Repository 주입 */
    @Binds
    @Singleton
    abstract fun bindOcrRepository(
        ocrRepositoryImpl: OcrRepositoryImpl
    ): OcrRepository

    /** 네트워크 상태 관련 Repository 주입 */
    @Binds
    @Singleton
    abstract fun bindNetworkStatusRepository(
        networkStatusRepositoryImpl: NetworkStatusRepositoryImpl
    ): NetworkStatusRepository

    /** 설정 화면 내 알람 권한 관련 Repository 주입 */
    @Binds
    @Singleton
    abstract fun bindSettingRepository(
        settingRepositoryImpl: SettingNotificationRepositoryImpl
    ): SettingNotificationRepository

    /** user Repository 주입 */
    @Binds
    @Singleton
    abstract fun bindLegacyUserRepository(
        legacyUserRepositoryImpl: LegacyUserRepositoryImpl
    ): LegacyUserRepository

    /** 신규 OCR 관련 Repository 주입 */
    @Binds
    @Singleton
    abstract fun bindNewOcrRepository(
        newOcrRepositoryImpl: NewOcrRepositoryImpl
    ): NewOcrRepository

    /** barcode 관련 Repository 주입 */
    @Binds
    @Singleton
    abstract fun bindBarcodeScannerRepository(
        barcodeScannerRepositoryImpl: BarcodeScannerRepositoryImpl
    ): BarcodeScannerRepository

    /** Wish Repository 주입 */
    @Binds
    @Singleton
    abstract fun bindWishRepository(
        wishRepositoryImpl: WishRepositoryImpl
    ): WishRepository

    /** UserRepository 주입 */
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}