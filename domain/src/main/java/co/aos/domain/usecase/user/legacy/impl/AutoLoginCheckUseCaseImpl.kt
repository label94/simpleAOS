package co.aos.domain.usecase.user.legacy.impl

import co.aos.domain.repository.LegacyUserRepository
import co.aos.domain.usecase.user.legacy.AutoLoginCheckUseCase
import javax.inject.Inject

/** 자동 로그인 관련 유스케이스 구현 클래스 */
class AutoLoginCheckUseCaseImpl @Inject constructor(
    private val legacyUserRepository: LegacyUserRepository
): AutoLoginCheckUseCase {
    override suspend fun invoke(): Boolean {
        return legacyUserRepository.isAutoLogin()
    }

    override suspend fun invoke(isAutoLogin: Boolean) {
        legacyUserRepository.setAutoLogin(isAutoLogin)
    }
}