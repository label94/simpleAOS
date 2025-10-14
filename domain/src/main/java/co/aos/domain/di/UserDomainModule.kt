package co.aos.domain.di

import co.aos.domain.usecase.user.renewal.CheckNicknameAvailableUseCase
import co.aos.domain.usecase.user.renewal.CheckUserIdAvailableUseCase
import co.aos.domain.usecase.user.renewal.EnableAutoLoginUseCase
import co.aos.domain.usecase.user.renewal.EnableIsSaveIdUseCase
import co.aos.domain.usecase.user.renewal.GetCurrentUserUseCase
import co.aos.domain.usecase.user.renewal.GetLoginIdUseCase
import co.aos.domain.usecase.user.renewal.IsAutoLoginUseCase
import co.aos.domain.usecase.user.renewal.IsSaveIdUseCase
import co.aos.domain.usecase.user.renewal.SetLoginIdUseCase
import co.aos.domain.usecase.user.renewal.SignInUseCase
import co.aos.domain.usecase.user.renewal.SignOutUseCase
import co.aos.domain.usecase.user.renewal.SignUpUseCase
import co.aos.domain.usecase.user.renewal.impl.CheckNicknameAvailableUseCaseImpl
import co.aos.domain.usecase.user.renewal.impl.CheckUserIdAvailableUseCaseImpl
import co.aos.domain.usecase.user.renewal.impl.EnableAutoLoginUseCaseImpl
import co.aos.domain.usecase.user.renewal.impl.EnableIsSaveIdUseCaseImpl
import co.aos.domain.usecase.user.renewal.impl.GetCurrentUserUseCaseImpl
import co.aos.domain.usecase.user.renewal.impl.GetLoginIdUseCaseImpl
import co.aos.domain.usecase.user.renewal.impl.IsAutoLoginUseCaseImpl
import co.aos.domain.usecase.user.renewal.impl.IsSaveIdUseCaseImpl
import co.aos.domain.usecase.user.renewal.impl.SetLoginIdUseCaseImpl
import co.aos.domain.usecase.user.renewal.impl.SignInUseCaseImpl
import co.aos.domain.usecase.user.renewal.impl.SignOutUseCaseImpl
import co.aos.domain.usecase.user.renewal.impl.SignUpUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** User 관련 di 주입 관리 모듈 */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class UserDomainModule {

    /** 회원가입 관련 di 주입 */
    @Binds
    @Singleton
    abstract fun bindSignUpUseCase(
        signUpUseCaseImpl: SignUpUseCaseImpl
    ): SignUpUseCase

    /** 로그인 관련 di 주입 */
    @Binds
    @Singleton
    abstract fun bindSignInUseCase(
        signInUseCaseImpl: SignInUseCaseImpl
    ): SignInUseCase

    /** 로그인한 정보 불러오기 관련 di 주입 */
    @Binds
    @Singleton
    abstract fun bindGetCurrentUserUseCase(
        getCurrentUserUseCaseImpl: GetCurrentUserUseCaseImpl
    ): GetCurrentUserUseCase

    /** 로그아웃 관련 di 주입 */
    @Binds
    @Singleton
    abstract fun bindSignOutUseCase(
        signOutUseCaseImpl: SignOutUseCaseImpl
    ): SignOutUseCase

    /** 닉네임 유효성 검증 di 주입 */
    @Binds
    @Singleton
    abstract fun bindCheckNicknameAvailableUseCase(
        checkNicknameAvailableUseCaseImpl: CheckNicknameAvailableUseCaseImpl
    ): CheckNicknameAvailableUseCase

    /** id 유효성 검증 di 주입 */
    @Binds
    @Singleton
    abstract fun bindCheckUserIdAvailableUseCase(
        checkUserIdAvailableUseCaseImpl: CheckUserIdAvailableUseCaseImpl
    ): CheckUserIdAvailableUseCase

    /** 자동 로그인 설정 관련 di 주입 */
    @Binds
    @Singleton
    abstract fun bindEnableAutoLoginUseCase(
        enableAutoLoginUseCaseImpl: EnableAutoLoginUseCaseImpl
    ): EnableAutoLoginUseCase

    /** 자동 로그인 설정 값 가져오기 관련 di 주입 */
    @Binds
    @Singleton
    abstract fun bindIsAutoLoginUseCase(
        isAutoLoginUseCaseImpl: IsAutoLoginUseCaseImpl
    ): IsAutoLoginUseCase

    /** id 저장 설정 활성화 관련 di 주입 */
    @Binds
    @Singleton
    abstract fun bindEnableIsSaveIdUseCase(
        enableIsSaveIdUseCaseImpl: EnableIsSaveIdUseCaseImpl
    ): EnableIsSaveIdUseCase

    /** id 저장 설정 값 가져오기 관련 di 주입 */
    @Binds
    @Singleton
    abstract fun bindIsSaveIdUseCase(
        isSaveIdUseCaseImpl: IsSaveIdUseCaseImpl
    ): IsSaveIdUseCase

    /** 로컬에 저장 된 로그인 id 반환 관련 di 주입 */
    @Binds
    @Singleton
    abstract fun bindGetLoginIdUseCase(
        getLoginIdUseCaseImpl: GetLoginIdUseCaseImpl
    ): GetLoginIdUseCase

    /** 로그인 id 저장 관련 di 주입 */
    @Binds
    @Singleton
    abstract fun bindSetLoginIdUseCase(
        setLoginIdUseCaseImpl: SetLoginIdUseCaseImpl
    ): SetLoginIdUseCase
}