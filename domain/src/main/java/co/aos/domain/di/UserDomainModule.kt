package co.aos.domain.di

import co.aos.domain.usecase.user.DeleteUserUseCase
import co.aos.domain.usecase.user.InsertUserUseCase
import co.aos.domain.usecase.user.LoginUseCase
import co.aos.domain.usecase.user.SearchUserInfoUseCase
import co.aos.domain.usecase.user.UpdateNickNameUseCase
import co.aos.domain.usecase.user.UpdatePasswordUseCase
import co.aos.domain.usecase.user.UpdateProfileImageUseCase
import co.aos.domain.usecase.user.impl.DeleteUserUseCaseImpl
import co.aos.domain.usecase.user.impl.InsertUserUseCaseImpl
import co.aos.domain.usecase.user.impl.LoginUseCaseImpl
import co.aos.domain.usecase.user.impl.SearchUserInfoUseCaseImpl
import co.aos.domain.usecase.user.impl.UpdateNickNameUseCaseImpl
import co.aos.domain.usecase.user.impl.UpdatePasswordUseCaseImpl
import co.aos.domain.usecase.user.impl.UpdateProfileImageUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * User Domain Di 모듈
 * */
@Module
@InstallIn(SingletonComponent::class)
internal abstract class UserDomainModule {

    /** 사용자 정보 저장 유스케이스 주입 */
    @Binds
    @Singleton
    abstract fun bindInsertUserUseCase(
        insertUserUseCaseImpl: InsertUserUseCaseImpl
    ): InsertUserUseCase

    /** 로그인 유스케이스 주입 */
    @Binds
    @Singleton
    abstract fun bindLoginUseCase(
        loginUseCaseImpl: LoginUseCaseImpl
    ): LoginUseCase

    /** 사용자 정보 가져오기 유스케이스 주입 */
    @Binds
    @Singleton
    abstract fun bindGetUserUseCase(
        getUserUseCaseImpl: SearchUserInfoUseCaseImpl
    ): SearchUserInfoUseCase

    /** 사용자 프로필 이미지 변경 유스케이스 주입 */
    @Binds
    @Singleton
    abstract fun bindUpdateProfileImagePathUseCase(
        updateUserUseCaseImpl: UpdateProfileImageUseCaseImpl
    ): UpdateProfileImageUseCase

    /** 사용자 닉네임 변경 유스케이스 주입 */
    @Binds
    @Singleton
    abstract fun bindUpdateNickNameUseCase(
        updateNickNameUseCaseImpl: UpdateNickNameUseCaseImpl
    ): UpdateNickNameUseCase

    /** 사용자 비밀번호 변경 유스케이스 주입 */
    @Binds
    @Singleton
    abstract fun bindUpdatePasswordUseCase(
        updatePasswordUseCaseImpl: UpdatePasswordUseCaseImpl
    ): UpdatePasswordUseCase

    /** 사용자 정보 삭제 유스케이스 주입 */
    @Binds
    @Singleton
    abstract fun bindDeleteUserUseCase(
        deleteUserUseCaseImpl: DeleteUserUseCaseImpl
    ): DeleteUserUseCase
}