package co.aos.domain.usecase.user.impl

import co.aos.domain.repository.UserRepository
import co.aos.domain.usecase.user.AutoLoginCheckUseCase
import javax.inject.Inject

/** 자동 로그인 관련 유스케이스 구현 클래스 */
class AutoLoginCheckUseCaseImpl @Inject constructor(
    private val userRepository: UserRepository
): AutoLoginCheckUseCase {
    override suspend fun invoke(): Boolean {
        return userRepository.isAutoLogin()
    }

    override suspend fun invoke(isAutoLogin: Boolean) {
        userRepository.setAutoLogin(isAutoLogin)
    }
}