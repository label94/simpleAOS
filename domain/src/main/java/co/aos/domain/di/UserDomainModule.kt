package co.aos.domain.di

import co.aos.domain.usecase.user.renewal.CheckNicknameAvailableUseCase
import co.aos.domain.usecase.user.renewal.CheckUserIdAvailableUseCase
import co.aos.domain.usecase.user.renewal.GetCurrentUserUseCase
import co.aos.domain.usecase.user.renewal.SignInUseCase
import co.aos.domain.usecase.user.renewal.SignOutUseCase
import co.aos.domain.usecase.user.renewal.SignUpUseCase
import co.aos.domain.usecase.user.renewal.impl.CheckNicknameAvailableUseCaseImpl
import co.aos.domain.usecase.user.renewal.impl.CheckUserIdAvailableUseCaseImpl
import co.aos.domain.usecase.user.renewal.impl.GetCurrentUserUseCaseImpl
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

    /** 닉네임 유효성 검증 주입 */
    @Binds
    @Singleton
    abstract fun bindCheckNicknameAvailableUseCase(
        checkNicknameAvailableUseCaseImpl: CheckNicknameAvailableUseCaseImpl
    ): CheckNicknameAvailableUseCase

    /** id 유효성 검증 주입 */
    @Binds
    @Singleton
    abstract fun bindCheckUserIdAvailableUseCase(
        checkUserIdAvailableUseCaseImpl: CheckUserIdAvailableUseCaseImpl
    ): CheckUserIdAvailableUseCase
}